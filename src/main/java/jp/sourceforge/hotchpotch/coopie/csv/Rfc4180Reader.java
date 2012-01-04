package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.logging.SimpleLog;
import jp.sourceforge.hotchpotch.coopie.util.Closable;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineImpl;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;

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
    private static final char SP = ' ';
    private static final String EMPTY_ELEM = "";

    protected boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private int recordNo_;
    private CharacterReadable reader_;
    private LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();

    private char elementSeparator_ = CsvSetting.COMMA;
    private char quoteMark_ = CsvSetting.DOUBLE_QUOTE;
    private boolean eof_;

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
    private LineReadable pushback_;
    private RecordBuffer rb_;

    @Override
    public String[] readRecord() {
        if (eof_) {
            return null;
        }

        reader_.mark();
        recordState_ = RecordState.VALID;
        rb_.clear();
        State state = State.INITIAL;

        // 要素区切り文字の次の位置
        int elemBeginPlain = 0;
        // 要素開始位置
        int beginPos = -1;
        // substring用
        int fromPos = -1;
        int elemEnd = -1;
        int i = 0;
        char[] bodyChars = null;
        StringBuilder elemBuff = null;
        try {
            read_loop: while (true) {
                final Line currentLine = readLine();
                if (currentLine == null) {
                    eof_ = true;
                    break read_loop;
                }
                fromPos = 0;

                bodyChars = currentLine.getBody().toCharArray();
                final String separator = currentLine.getSeparator()
                        .getSeparator();
                final char[] endChars = separator.toCharArray();
                i = 0;
                for (; i < bodyChars.length; i++) {
                    final char c = bodyChars[i];
                    switch (state) {
                    case INITIAL:
                        if (c == quoteMark_) {
                            state = State.QUOTED_ELEMENT;
                            rb_.startRecord();
                            rb_.startElement();
                            fromPos = beginPos = i + 1;
                            elemBeginPlain = i;
                        } else if (c == elementSeparator_) {
                            state = State.BEGIN_ELEMENT;
                            rb_.startRecord();
                            rb_.startElement();
                            rb_.endElement(EMPTY_ELEM);
                            elemBeginPlain = i + 1;
                        } else if (c == SP) {
                            state = State.BEGIN_ELEMENT;
                            rb_.startRecord();
                        } else {
                            state = State.UNQUOTED_ELEMENT;
                            rb_.startRecord();
                            rb_.startElement();
                            fromPos = beginPos = i;
                            elemBeginPlain = i;
                        }
                        break;

                    case BEGIN_ELEMENT:
                        if (c == quoteMark_) {
                            state = State.QUOTED_ELEMENT;
                            // クォートが要素の先頭に登場したとき、それより前のspaceを除く。
                            rb_.startElement();
                            fromPos = beginPos = i + 1;
                        } else if (c == elementSeparator_) {
                            rb_.startElement();
                            if (elemBeginPlain != i) {
                                fromPos = beginPos = elemBeginPlain;
                            } else {
                                fromPos = beginPos = i;
                            }
                            final String elem = new String(bodyChars, fromPos,
                                    i - fromPos);
                            rb_.endElement(elem);
                            elemBeginPlain = i + 1;
                        } else if (c == SP) {
                        } else {
                            state = State.UNQUOTED_ELEMENT;
                            rb_.startElement();
                            if (elemBeginPlain != i) {
                                fromPos = beginPos = elemBeginPlain;
                            } else {
                                fromPos = beginPos = i;
                            }
                            //rb.appendPlain(c);
                        }
                        break;

                    case UNQUOTED_ELEMENT:
                        //rb.appendPlain(c);
                        if (c == quoteMark_) {
                        } else if (c == elementSeparator_) {
                            final String elem = new String(bodyChars, fromPos,
                                    i - fromPos);
                            rb_.endElement(elem);
                            elemBeginPlain = i + 1;
                            state = State.BEGIN_ELEMENT;
                        }
                        break;

                    case QUOTED_ELEMENT:
                        if (c == quoteMark_) {
                            elemEnd = i;
                            state = State.QUOTE;
                        }
                        break;

                    case QUOTE:
                        if (c == quoteMark_ && elemEnd + 1 == i) {
                            if (elemBuff == null) {
                                elemBuff = new StringBuilder();
                            }
                            elemBuff.append(bodyChars, fromPos, i - fromPos);
                            fromPos = i + 1;

                            state = State.QUOTED_ELEMENT;
                        } else if (c == SP) {
                            // 要素より後のspaceを捨てるため
                        } else if (c == elementSeparator_) {
                            String elem = new String(bodyChars, fromPos,
                                    elemEnd - fromPos);
                            if (elemBuff != null) {
                                elemBuff.append(elem);
                                elem = elemBuff.toString();
                                elemBuff = null;
                            }
                            rb_.endElement(elem);
                            elemBeginPlain = i + 1;
                            state = State.BEGIN_ELEMENT;
                        } else {
                            /*
                             * クォートされた要素内にクォートが登場したら、
                             * 続く文字はクォート(→エスケープされたクォート文字とみなす)か、改行(→レコードの終わりとみなす)
                             * であるべき。
                             * だが、通常の文字が入力されてしまった。
                             * "abc"d" ... このような入力
                             */
                            final SimpleLog log = new SimpleLog();
                            log.append("invalid record: recordNo={}, ",
                                    recordNo_);
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
                            /*
                             * と上のコメントに書いてあるが、
                             * 先頭のスペースを捨てていた場合はINVALIDにしない、という動きをするように見える。
                             */
                            // クォート文字の1文字ぶん差がある
                            if (beginPos != elemBeginPlain + 1) {
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
                            final Iterator<Line> it = lines.iterator();
                            final StringBuilder sb = new StringBuilder();
                            final Line line1 = it.next();
                            sb.append(line1.getBodyAndSeparator().substring(
                                    elemBeginPlain));
                            while (it.hasNext()) {
                                final Line l = it.next();
                                sb.append(l.getBodyAndSeparator());
                            }
                            pushback_ = new LineReadable(new StringReader(
                                    sb.toString()));
                            state = State.UNQUOTED_ELEMENT;
                            elemBuff = null;
                            continue read_loop;
                        }
                        break;

                    default:
                        throw new AssertionError();
                    }
                }

                /* body終了 */
                if (state == State.QUOTED_ELEMENT) {
                    // 改行を含む要素
                    if (elemBuff == null) {
                        elemBuff = new StringBuilder();
                    }
                    elemBuff.append(bodyChars, fromPos, i - fromPos);
                    fromPos = i;
                    elemBuff.append(endChars);
                    continue read_loop;
                }
                break read_loop;
            }

            // 終了処理
            switch (state) {
            case INITIAL:
                break;
            case BEGIN_ELEMENT:
                rb_.startElement();
                if (elemBeginPlain != i) {
                    fromPos = beginPos = elemBeginPlain;
                    // 空白だけの要素で行が終わる場合も、ここでフォロー
                    final String elem = new String(bodyChars, fromPos, i
                            - fromPos);
                    rb_.endElement(elem);
                } else {
                    rb_.endElement(EMPTY_ELEM);
                }
                rb_.endRecord();
                break;
            case UNQUOTED_ELEMENT:
                if (rb_.isInElement()) {
                    final String elem = new String(bodyChars, fromPos, i
                            - fromPos);
                    rb_.endElement(elem);
                }
                rb_.endRecord();
                break;
            case QUOTED_ELEMENT:
                // クォート文字しかないrecordの場合
                if (rb_.isEmpty()) {
                    //rb_.append(quoteMark_);
                    rb_.endElement(String.valueOf(quoteMark_));
                }
                // クォートされた要素の途中でEOFになった場合
                if (rb_.isInElement()) {
                    String elem = new String(bodyChars, fromPos, i - fromPos);
                    if (elemBuff != null) {
                        elemBuff.append(elem);
                        elem = elemBuff.toString();
                        elemBuff = null;
                    }
                    rb_.endElement(elem);
                    recordState_ = RecordState.INVALID;
                }
                rb_.endRecord();
                break;
            case QUOTE:
                if (rb_.isInElement()) {
                    // クォートされていたら最後のスペースは除く
                    String elem = new String(bodyChars, fromPos, elemEnd
                            - fromPos);
                    if (elemBuff != null) {
                        elemBuff.append(elem);
                        elem = elemBuff.toString();
                        elemBuff = null;
                    }
                    rb_.endElement(elem);
                }
                rb_.endRecord();
                break;
            default:
                throw new AssertionError();
            }

            if (eof_ && rb_.isEmpty()) {
                return null;
            }

            recordNo_++;
            final String[] record = rb_.toRecord();
            return record;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private Line readLine() throws IOException {
        if (pushback_ != null) {
            final Line l = pushback_.readLine();
            if (l != null) {
                return l;
            }
            CloseableUtil.closeNoException(pushback_);
            pushback_ = null;
        }
        return reader_.readLine();
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

    static class RecordBuffer implements ElementParserContext {

        private final List<String> elems_ = CollectionsUtil.newArrayList();
        /*
         * クォートされた要素がパースエラーだった場合に使用する。
         * 要素区切り文字の次の文字から、その要素を終える区切り文字(or 改行)の手前までを持つ。
         */
        private boolean inElement_;
        private boolean inRecord_;

        public void clear() {
            elems_.clear();
            inElement_ = false;
            inRecord_ = false;
        }

        public void startRecord() {
            assertNotInRecord();
            inRecord_ = true;
        }

        public void endRecord() {
            assertInRecord();
            assertNotInElement();
            inRecord_ = false;
        }

        public void startElement() {
            assertInRecord();
            assertNotInElement();
            inElement_ = true;
        }

        public void endElement() {
            assertInRecord();
            assertInElement();
            //elems_.add(bufferString());
            inElement_ = false;
        }

        public void endElement(final String elem) {
            assertInRecord();
            assertInElement();
            elems_.add(elem);
            inElement_ = false;
        }

        @Override
        public boolean isInElement() {
            return inElement_;
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

        private final LineReadable reader_;
        private final LineReaderHandler lineReaderHandler_;

        private boolean closed_;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);
        private final Line line_ = new LineImpl();
        private final Marker marker_ = new Marker();
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
            marker_.clear();
        }

        public List<Line> getMarkedLines() {
            return marker_.getMarkedLines();
        }

        public Line readLine() throws IOException {
            while (true) {
                final Line line = lineReaderHandler_.readLine(reader_, line_);
                if (line == null) {
                    return null;
                }
                if (lineReaderHandler_.acceptLine(line, parserContext_)) {
                    marker_.mark(line);
                    return line;
                }
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

            private final List<Line> lines_ = CollectionsUtil.newLinkedList();

            public void mark(final Line line) {
                final Line l = new LineImpl(line.getBody(), line.getNumber(),
                        line.getSeparator());
                lines_.add(l);
            }

            public List<Line> getMarkedLines() {
                final LinkedList<Line> lines = new LinkedList<Line>();
                lines.addAll(lines_);
                return lines;
            }

            public void clear() {
                lines_.clear();
            }

        }

    }

    public enum RecordState {
        VALID, INVALID
    }

}
