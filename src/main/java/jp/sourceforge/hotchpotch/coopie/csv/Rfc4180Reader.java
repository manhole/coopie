package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.logging.SimpleLog;
import jp.sourceforge.hotchpotch.coopie.util.BufferedReadable;
import jp.sourceforge.hotchpotch.coopie.util.Closable;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineImpl;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;
import jp.sourceforge.hotchpotch.coopie.util.LineSeparator;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

/**
 * CSV Reader
 * 
 * http://www.rfc-editor.org/rfc/rfc4180.txt
 * http://www.kasai.fm/wiki/rfc4180jp (日本語訳)
 * 
 * @author manhole
 */
public class Rfc4180Reader implements ElementReader {

    private static final Logger logger = LoggerFactory.getLogger();
    private static final char CR = CsvSetting.CR;
    private static final char LF = CsvSetting.LF;
    private static final char SP = ' ';

    protected boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private int recordNo_;
    private CharacterReadable reader_;
    private LineReaderHandler lineReaderHandler_ = PassThroughLineReaderHandler
            .getInstance();

    private char elementSeparator_ = CsvSetting.COMMA;
    private char quoteMark_ = CsvSetting.DOUBLE_QUOTE;

    public void open(final Readable readable) {
        rb_ = new RecordBuffer();
        reader_ = new CharacterReadable(readable, lineReaderHandler_, rb_);
        closed_ = false;
    }

    @Override
    public int getRecordNumber() {
        return recordNo_;
    }

    @Override
    public int getLineNumber() {
        return reader_.getLineNumber();
    }

    public RecordState getRecordState() {
        return recordState_;
    }

    private RecordState recordState_;
    private BufferedReadable pushback_;
    private RecordBuffer rb_;

    @Override
    public String[] readRecord() {
        if (isEof()) {
            return null;
        }

        reader_.mark();
        recordState_ = RecordState.OK;
        rb_.clear();
        State state = State.INITIAL;

        try {
            read_loop: for (char c = next(); !isEof(); c = next()) {
                switch (state) {
                case INITIAL:
                    if (c == quoteMark_) {
                        state = State.QUOTED_ELEMENT;
                        rb_.startRecord();
                        rb_.startElement();
                        rb_.appendPlain(c);
                    } else if (c == elementSeparator_) {
                        state = State.BEGIN_ELEMENT;
                        rb_.startRecord();
                        rb_.startElement();
                        rb_.endElement();
                    } else if (c == SP) {
                        state = State.BEGIN_ELEMENT;
                        rb_.startRecord();
                        rb_.pendingSpace(c);
                        rb_.appendPlain(c);
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        break read_loop;
                    } else if (c == LF) {
                        break read_loop;
                    } else {
                        state = State.UNQUOTED_ELEMENT;
                        rb_.startRecord();
                        rb_.startElement();
                        rb_.append(c);
                    }
                    break;

                case BEGIN_ELEMENT:
                    if (c == quoteMark_) {
                        state = State.QUOTED_ELEMENT;
                        // クォートが要素の先頭に登場したとき、それより前のspaceを除く。
                        rb_.discardHeadingSpace();
                        rb_.startElement();
                        rb_.appendPlain(c);
                    } else if (c == elementSeparator_) {
                        rb_.startElement();
                        rb_.endElement();
                    } else if (c == SP) {
                        rb_.pendingSpace(c);
                        rb_.appendPlain(c);
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        rb_.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else if (c == LF) {
                        rb_.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else {
                        state = State.UNQUOTED_ELEMENT;
                        rb_.startElement();
                        rb_.append(c);
                        //rb.appendPlain(c);
                    }
                    break;

                case UNQUOTED_ELEMENT:
                    //rb.appendPlain(c);
                    if (c == quoteMark_) {
                        rb_.append(c);
                    } else if (c == elementSeparator_) {
                        rb_.endElement();
                        state = State.BEGIN_ELEMENT;
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        rb_.endElement();
                        rb_.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else if (c == LF) {
                        rb_.endElement();
                        rb_.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else {
                        rb_.append(c);
                    }
                    break;

                case QUOTED_ELEMENT:
                    rb_.appendPlain(c);
                    if (c == quoteMark_) {
                        state = State.QUOTE;
                    } else {
                        rb_.append(c);
                    }
                    break;

                case QUOTE:
                    rb_.appendPlain(c);
                    if (c == quoteMark_ && !rb_.hasPendingSpace()) {
                        rb_.append(c);
                        state = State.QUOTED_ELEMENT;
                    } else if (c == SP) {
                        rb_.pendingSpace(c);
                    } else if (c == elementSeparator_) {
                        rb_.discardPending();
                        rb_.endElement();
                        state = State.BEGIN_ELEMENT;
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        rb_.discardPending();
                        rb_.endElement();
                        rb_.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else if (c == LF) {
                        rb_.discardPending();
                        rb_.endElement();
                        rb_.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else {
                        /*
                         * クォートされた要素内にクォートが登場したら、
                         * 続く文字はクォート(→エスケープされたクォート文字とみなす)か、改行(→レコードの終わりとみなす)
                         * であるべき。
                         * だが、通常の文字が入力されてしまった。
                         * "abc"d" ... このような入力
                         */
                        final SimpleLog log = new SimpleLog();
                        log.append("invalid record: recordNo={}, ", recordNo_);
                        final List<Line> lines = reader_.getMarkedLines();
                        log.appendFormat("line=");
                        boolean first = true;
                        for (final Line line : lines) {
                            if (first) {
                                first = !first;
                            } else {
                                log.appendFormat(",");
                            }
                            log.append("{}[{}]", line.getNumber(),
                                    line.getBody());
                        }

                        /*
                         * 先頭のスペースを除いた、クォートされた要素の場合は、
                         * クォートされない要素として読み直す。
                         * 
                         * 先頭ではなく末尾にスペースを持つ要素の場合は、INVALIDにはしない。
                         * (グレーだけれど)
                         */
                        if (rb_.isDiscardedHeadingSpace()) {
                            logger.debug(log);
                        } else {
                            logger.warn(log);
                            recordState_ = RecordState.INVALID;
                        }

                        /*
                         * ここでは、要素自体がクォートされていないものとして扱うことにする。
                         * つまり、"abc"d" このような入力を、"abc"d" そのものとみなすということ。
                         * 恐らく "abc""d" の誤りと思われるが、そこまで判断できない。
                         * (見直す可能性アリ)
                         */
                        pushback_ = new BufferedReadable(new StringReader(
                                rb_.getPlain()));
                        rb_.clearElement();
                        state = State.UNQUOTED_ELEMENT;
                    }
                    break;

                default:
                    throw new AssertionError();
                }

            }

            // 終了処理
            switch (state) {
            case INITIAL:
                break;
            case BEGIN_ELEMENT:
                rb_.startElement();
                rb_.endElement();
                rb_.endRecord();
                break;
            case UNQUOTED_ELEMENT:
                if (rb_.isInElement()) {
                    rb_.endElement();
                }
                rb_.endRecord();
                break;
            case QUOTED_ELEMENT:
                // クォート文字しかないrecordの場合
                if (rb_.isEmpty()) {
                    rb_.append(quoteMark_);
                    rb_.endElement();
                }
                // クォートされた要素の途中でEOFになった場合
                if (rb_.isInElement()) {
                    rb_.endElement();
                    recordState_ = RecordState.INVALID;
                }
                rb_.endRecord();
                break;
            case QUOTE:
                if (rb_.isInElement()) {
                    // クォートされていたら最後のスペースは除く
                    rb_.discardPending();
                    rb_.endElement();
                }
                rb_.endRecord();
                break;
            default:
                throw new AssertionError();
            }

            if (isEof() && rb_.isEmpty()) {
                return null;
            }

            recordNo_++;
            final String[] record = rb_.toRecord();
            return record;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private void consumeFollowLfIfPossible() throws IOException {
        if (peek() == LF) {
            next();
        }
    }

    private char peek() throws IOException {
        if (pushback_ != null) {
            final char c = pushback_.peekChar();
            if (pushback_.isEof()) {
                CloseableUtil.closeNoException(pushback_);
                pushback_ = null;
            } else {
                return c;
            }
        }
        return reader_.peekChar();
    }

    private char next() throws IOException {
        if (pushback_ != null) {
            final char c = pushback_.readChar();
            if (pushback_.isEof()) {
                CloseableUtil.closeNoException(pushback_);
                pushback_ = null;
            } else {
                return c;
            }
        }
        return reader_.readChar();
    }

    /*
     * EOF
     */
    private boolean isEof() {
        return reader_.isEof();
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        CloseableUtil.closeNoException(reader_);
    }

    public void setElementSeparator(final char elementSeparator) {
        elementSeparator_ = elementSeparator;
    }

    public void setQuoteMark(final char quoteMark) {
        quoteMark_ = quoteMark;
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

    private static void clearBuffer(final StringBuilder sb) {
        if (sb != null) {
            sb.setLength(0);
        }
    }

    static class RecordBuffer implements ElementParserContext {

        private final List<String> elems_ = CollectionsUtil.newArrayList();
        private final StringBuilder sb_ = new StringBuilder();
        /*
         * クォートされた要素がパースエラーだった場合に使用する。
         * 要素区切り文字の次の文字から、その要素を終える区切り文字(or 改行)の手前までを持つ。
         */
        private StringBuilder plainElement_;
        private StringBuilder pending_;
        private boolean discardedHeadingSpace_;
        private boolean inElement_;
        private boolean inRecord_;

        public void clear() {
            elems_.clear();
            clearBuffer(sb_);
            clearBuffer(plainElement_);
            clearBuffer(pending_);
            discardedHeadingSpace_ = false;
            inElement_ = false;
            inRecord_ = false;
        }

        public void startRecord() {
            assertNotInRecord();
            inRecord_ = true;
        }

        public void endRecord() {
            // 空白だけの要素で行が終わる場合をフォロー
            if (pending_ != null) {
                startElement();
                endElement();
            }
            assertInRecord();
            assertNotInElement();
            inRecord_ = false;
        }

        public void startElement() {
            assertInRecord();
            assertNotInElement();
            inElement_ = true;
            flushPending();
        }

        public void endElement() {
            assertInRecord();
            assertInElement();
            elems_.add(bufferString());
            inElement_ = false;
            clearBuffer(plainElement_);
        }

        @Override
        public boolean isInElement() {
            return inElement_;
        }

        public boolean isBufferEmpty() {
            if (sb_ == null || sb_.length() == 0) {
                return true;
            }
            return false;
        }

        public void append(final char c) {
            assertInElement();
            sb_.append(c);
        }

        public void appendPlain(final char c) {
            if (plainElement_ == null) {
                plainElement_ = new StringBuilder();
            }
            plainElement_.append(c);
        }

        public void clearElement() {
            clearBuffer(sb_);
            discardPending();
            clearBuffer(plainElement_);
        }

        /*
         * 両端のスペースを一時的に持つ。
         * 状態によってtrim扱いとするため。
         */
        public void pendingSpace(final char c) {
            if (pending_ == null) {
                pending_ = new StringBuilder();
            }
            pending_.append(c);
        }

        public void discardHeadingSpace() {
            if (pending_ != null) {
                discardedHeadingSpace_ = true;
                pending_ = null;
            }
        }

        public void discardPending() {
            pending_ = null;
        }

        public boolean hasPendingSpace() {
            if (pending_ != null) {
                return true;
            }
            return false;
        }

        private void flushPending() {
            if (pending_ != null) {
                final int len = pending_.length();
                for (int i = 0; i < len; i++) {
                    append(pending_.charAt(i));
                }
                pending_ = null;
            }
        }

        private String bufferString() {
            final String s = sb_.toString();
            clearBuffer(sb_);
            return s;
        }

        public boolean isEmpty() {
            return elems_.isEmpty();
        }

        public String[] toRecord() {
            assertNotInRecord();
            return elems_.toArray(new String[elems_.size()]);
        }

        private void assertInRecord() {
            if (!inRecord_) {
                throw new IllegalStateException("not in record");
            }
        }

        private void assertNotInRecord() {
            if (inRecord_) {
                throw new IllegalStateException("in record");
            }
        }

        private void assertInElement() {
            if (!isInElement()) {
                throw new IllegalStateException("not started");
            }
        }

        private void assertNotInElement() {
            if (isInElement()) {
                throw new IllegalStateException("already started");
            }
        }

        public boolean isDiscardedHeadingSpace() {
            return discardedHeadingSpace_;
        }

        public String getPlain() {
            return plainElement_.toString();
        }

    }

    static enum State {

        /**
         * 行を開始する前の状態
         */
        INITIAL,

        /**
         * 要素を開始した状態。
         * 要素が区切り文字で終了した直後もこの状態となる。(区切り文字の直後は次の要素の開始であるため)
         */
        BEGIN_ELEMENT,

        /**
         * クォートされていない要素の最中
         */
        UNQUOTED_ELEMENT,

        /**
         * クォートされている要素の最中
         */
        QUOTED_ELEMENT,

        /**
         * クォートされている要素の中でクォート文字が登場した状態
         */
        QUOTE

    }

    private static class CharacterReadable implements Closable {

        private static final char NULL_CHAR = '\u0000';
        private static final char[] EMPTY_CHARS = new char[] {};
        private final LineReadable reader_;
        private final LineReaderHandler lineReaderHandler_;
        private char[] chars_ = EMPTY_CHARS;
        private int pos_;
        private boolean eof_;

        private boolean closed_;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);
        private String line_;
        private Marker marker_;
        private final ElementParserContext parserContext_;

        CharacterReadable(final Readable readable,
                final LineReaderHandler lineReaderHandler,
                final ElementParserContext parserContext) {
            reader_ = new LineReadable(readable);
            lineReaderHandler_ = lineReaderHandler;
            parserContext_ = parserContext;
        }

        /*
         * mark可能なのは、行の境目だけ。
         */
        public void mark() {
            marker_ = new Marker();
        }

        public List<Line> getMarkedLines() {
            return marker_.getMarkedLines();
        }

        public boolean isEof() {
            return eof_;
        }

        public char readChar() throws IOException {
            readNextIfNeed();
            if (eof_) {
                return NULL_CHAR;
            }
            final char c = chars_[pos_];
            pos_++;
            return c;
        }

        public char peekChar() throws IOException {
            readNextIfNeed();
            if (eof_) {
                return NULL_CHAR;
            }
            final char c = chars_[pos_];
            return c;
        }

        protected void readNextIfNeed() throws IOException {
            while (!eof_ && chars_.length <= pos_) {
                final Line line = lineReaderHandler_.readLine(reader_);
                if (line == null) {
                    eof_ = true;
                    return;
                }
                if (!lineReaderHandler_.acceptLine(line, parserContext_)) {
                    continue;
                }

                line_ = line.getBody();
                final LineSeparator sep = line.getSeparator();
                final String end = sep.getSeparator();
                chars_ = (line_ + end).toCharArray();
                pos_ = 0;
                marker_.mark(reader_.getLineNumber(), line_);
            }
        }

        public int getLineNumber() {
            return reader_.getLineNumber();
        }

        @Override
        public boolean isClosed() {
            return closed_;
        }

        @Override
        public void close() throws IOException {
            closed_ = true;
            CloseableUtil.closeNoException(reader_);
        }

        private static class Marker {

            private int beginLineNumber_ = -1;
            private List<String> lines_;

            public void mark(final int lineNumber, final String line) {
                if (lines_ == null) {
                    beginLineNumber_ = lineNumber;
                    lines_ = new LinkedList<String>();
                }
                lines_.add(line);
            }

            public List<Line> getMarkedLines() {
                int num = beginLineNumber_;
                final LinkedList<Line> lines = new LinkedList<Line>();
                for (final String s : lines_) {
                    final Line l = new LineImpl(s, num, null);
                    lines.add(l);
                    num++;
                }
                return lines;
            }

        }

    }

    public enum RecordState {
        OK, INVALID
    }

}
