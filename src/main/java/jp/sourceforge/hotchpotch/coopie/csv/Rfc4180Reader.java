package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.StringReader;
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
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

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

    private RecordState recordState_;
    private LineReadable pushback_;
    private RecordBuffer rb_;

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

    @Override
    public String[] readRecord() {
        if (eof_) {
            return null;
        }

        recordState_ = RecordState.VALID;
        rb_.clear();
        try {
            return readRecord0();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private String[] readRecord0() throws IOException {
        final ReadContect rc = new ReadContect();

        read_loop: while (true) {
            final Line line = readLine();
            rc.line = line;
            if (line == null) {
                eof_ = true;
            } else {
                rc.fromPos = 0;
                rc.body = line.getBody();
                final char[] bodyChars = rc.body.toCharArray();
                final int length = bodyChars.length;
                rc.pos = 0;
                for (; rc.pos < length; rc.pos++) {
                    final char c = bodyChars[rc.pos];
                    switch (rc.state) {
                    case INITIAL:
                        if (c == quoteMark_) {
                            rc.state = State.QUOTED_ELEMENT;
                            rb_.startRecord();
                            rb_.startElement();
                            rc.fromPos = rc.beginPos = rc.pos + 1;
                            rc.elemBeginPlain = rc.pos;
                        } else if (c == elementSeparator_) {
                            rc.state = State.BEGIN_ELEMENT;
                            rb_.startRecord();
                            rb_.startElement();
                            rb_.endElement(EMPTY_ELEM);
                            rc.elemBeginPlain = rc.pos + 1;
                        } else if (c == SP) {
                            rc.state = State.BEGIN_ELEMENT;
                            rb_.startRecord();
                        } else {
                            rc.state = State.UNQUOTED_ELEMENT;
                            rb_.startRecord();
                            rb_.startElement();
                            rc.fromPos = rc.beginPos = rc.pos;
                            rc.elemBeginPlain = rc.pos;
                        }
                        break;

                    case BEGIN_ELEMENT:
                        if (c == quoteMark_) {
                            rc.state = State.QUOTED_ELEMENT;
                            // クォートが要素の先頭に登場したとき、それより前のspaceを除く。
                            rb_.startElement();
                            rc.fromPos = rc.beginPos = rc.pos + 1;
                        } else if (c == elementSeparator_) {
                            rb_.startElement();
                            // 空白だけの要素をフォロー
                            rc.fromPos = rc.beginPos = rc.elemBeginPlain;
                            final String elem = rc.body.substring(rc.fromPos,
                                    rc.pos);
                            rb_.endElement(elem);
                            rc.elemBeginPlain = rc.pos + 1;
                        } else if (c == SP) {
                            // 区切り文字直後のspaceを捨てられるように
                        } else {
                            rc.state = State.UNQUOTED_ELEMENT;
                            rb_.startElement();
                            // spaceがあれば、要素先頭のspaceを残す(捨てない)
                            rc.fromPos = rc.beginPos = rc.elemBeginPlain;
                        }
                        break;

                    case UNQUOTED_ELEMENT:
                        if (c == elementSeparator_) {
                            final String elem = rc.body.substring(rc.fromPos,
                                    rc.pos);
                            rb_.endElement(elem);
                            rc.elemBeginPlain = rc.pos + 1;
                            rc.state = State.BEGIN_ELEMENT;
                        }
                        break;

                    case QUOTED_ELEMENT:
                        if (c == quoteMark_) {
                            // 閉じ候補クォートの手前位置を記録
                            rc.elemEnd = rc.pos;
                            rc.state = State.QUOTE;
                        }
                        break;

                    case QUOTE:
                        if (c == quoteMark_ && rc.elemEnd + 1 == rc.pos) {
                            /*
                             * 閉じ候補クォートの直後に再度クォート文字が登場した場合
                             * ["abc""...]
                             * 2つ連続したクォートを1クォート文字として扱う
                             */
                            if (rc.elemBuff == null) {
                                rc.elemBuff = new StringBuilder();
                            }
                            rc.elemBuff.append(rc.body.substring(rc.fromPos,
                                    rc.pos));
                            rc.fromPos = rc.pos + 1;

                            rc.state = State.QUOTED_ELEMENT;
                        } else if (c == SP) {
                            // 閉じ候補クォートより後のspaceを捨てるため
                        } else if (c == elementSeparator_) {
                            /*
                             * 閉じクォートの後(直後 or spaceを間に含んでいる)に、要素区切り文字が登場。
                             * 
                             * 閉じクォートの手前位置までをデータとして取得。
                             */
                            String elem = rc.body.substring(rc.fromPos,
                                    rc.elemEnd);
                            if (rc.elemBuff != null) {
                                rc.elemBuff.append(elem);
                                elem = rc.elemBuff.toString();
                                rc.elemBuff = null;
                            }
                            rb_.endElement(elem);
                            rc.elemBeginPlain = rc.pos + 1;
                            rc.state = State.BEGIN_ELEMENT;
                        } else {
                            invalid(rc);
                            continue read_loop;
                        }
                        break;

                    default:
                        throw new AssertionError();
                    }
                }
            }

            // 終了処理(行の終わり or EOF)
            switch (rc.state) {
            case INITIAL:
                break;
            case BEGIN_ELEMENT:
                rb_.startElement();
                rc.fromPos = rc.beginPos = rc.elemBeginPlain;
                // 空白だけの要素で行が終わる場合も、ここでフォロー
                {
                    final String elem = rc.body.substring(rc.fromPos, rc.pos);
                    rb_.endElement(elem);
                }
                rb_.endRecord();
                break;
            case UNQUOTED_ELEMENT:
                if (rb_.isInElement()) {
                    final String elem = rc.body.substring(rc.fromPos, rc.pos);
                    rb_.endElement(elem);
                }
                rb_.endRecord();
                break;
            case QUOTED_ELEMENT:
                if (eof_) {
                    // クォート文字しかないrecordの場合
                    if (rb_.isEmpty()) {
                        // クォートの手前にspaceがある場合は、spaceも含める
                        final String s = rc.body.substring(rc.elemBeginPlain,
                                rc.pos);
                        rb_.endElement(s);
                    }
                    // クォートされた要素の途中でEOFになった場合
                    if (rb_.isInElement()) {
                        invalid(rc);
                        continue read_loop;
                    }
                    rb_.endRecord();
                } else {
                    // 改行を含む要素
                    if (rc.elemBuff == null) {
                        rc.elemBuff = new StringBuilder();
                    }
                    rc.elemBuff.append(rc.body.substring(rc.fromPos, rc.pos));
                    rc.fromPos = rc.pos;
                    final String separator = rc.line.getSeparator()
                            .getSeparator();
                    rc.elemBuff.append(separator);

                    if (rc.savedLines == null) {
                        rc.savedLines = CollectionsUtil.newArrayList();
                    }
                    rc.savedLines.add(copyLine(rc.line));
                    continue read_loop;
                }

                break;
            case QUOTE:
                if (rb_.isInElement()) {
                    // クォートされていたら最後のspaceは除く
                    String elem = rc.body.substring(rc.fromPos, rc.elemEnd);
                    if (rc.elemBuff != null) {
                        rc.elemBuff.append(elem);
                        elem = rc.elemBuff.toString();
                        rc.elemBuff = null;
                    }
                    rb_.endElement(elem);
                }
                rb_.endRecord();
                break;
            default:
                throw new AssertionError();
            }

            break read_loop;
        }

        if (eof_ && rb_.isEmpty()) {
            return null;
        }

        recordNo_++;
        final String[] record = rb_.toRecord();
        return record;
    }

    private void invalid(final ReadContect rc) {
        /*
         * クォートされた要素内にクォートが登場したら、
         * 続く文字はクォート(→エスケープされたクォート文字とみなす)か、改行(→レコードの終わりとみなす)
         * であるべき。
         * だが、通常の文字が入力されてしまった。
         * "abc"d" ... このような入力
         */
        final SimpleLog log = new SimpleLog();
        log.append("invalid record: recordNo={}, ", recordNo_);
        log.appendFormat("line=");

        final List<Line> readingLines = CollectionsUtil.newArrayList();
        if (rc.savedLines != null) {
            readingLines.addAll(rc.savedLines);
        }
        if (!eof_) {
            readingLines.add(rc.line);
        }

        {
            boolean first = true;
            for (final Line l : readingLines) {
                if (first) {
                    first = false;
                } else {
                    log.appendFormat(",");
                }
                log.append("{}[{}]", l.getNumber(), l.getBody());
            }
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
         * 
         * クォート関係が不正でも、要素がspace始まりだった場合は、
         * spaceを要素に含みクォートを単なる文字として扱う。
         */
        // クォート文字の1文字ぶん差がある
        if (rc.beginPos != rc.elemBeginPlain + 1) {
            logger.debug(log);
        } else {
            // 先頭がspaceではない
            logger.warn(log);
            recordState_ = RecordState.INVALID;
        }

        /*
         * ここでは、要素自体がクォートされていないものとして扱うことにする。
         * つまり、"abc"d" このような入力を、"abc"d" そのものとみなすということ。
         * 恐らく "abc""d" の誤りと思われるが、そこまで判断できない。
         * (見直す可能性アリ)
         */

        rc.state = State.UNQUOTED_ELEMENT;
        rc.elemBuff = null;

        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (final Line l : readingLines) {
            if (first) {
                first = false;
                sb.append(l.getBodyAndSeparator().substring(rc.elemBeginPlain));
            } else {
                sb.append(l.getBodyAndSeparator());
            }
        }
        pushback_ = new LineReadable(new StringReader(sb.toString()));
        rc.savedLines = null;
        rc.elemBuff = null;
        if (eof_) {
            eof_ = false;
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

    private Line copyLine(final Line line) {
        final Line l = new LineImpl(line.getBody(), line.getNumber(),
                line.getSeparator());
        return l;
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

    static class ReadContect {
        State state = State.INITIAL;
        /*
         * 要素区切り文字の次の位置
         * - 要素がクォートされている場合は、先頭のクォートの手前の位置。
         *   要素BBの場合
         *   [AA,BB]   => 3
         *   [AA,"BB"] => 3
         *   [AA, "BB"] => 3
         */
        int elemBeginPlain = 0;
        /*
         * 要素データの開始位置。
         * クォートされた要素の手前にスペースが登場したかの判定にだけ使用している
         * - 要素がクォートされている場合は、先頭のクォートの次の位置
         * - 要素がクォートされない場合は、elemBeginPlainと同じ値になる
         *   [AA,BB]    => 3
         *   [AA,"BB"]  => 4
         *   [AA, "BB"] => 5
         */
        int beginPos = -1;
        /*
         * substring用
         * ほぼbeginPosと同じ値
         * 
         * クォート文字をデータに含む場合に、エスケープクォート途中までを前の行までをいったんバッファへ入れ、
         * 後続の位置を記録するのに使う。
         */
        int fromPos = -1;
        int elemEnd = -1;
        int pos = 0;
        StringBuilder elemBuff;
        // 改行を含む行の場合に、改行前までのLineを保持する
        List<Line> savedLines;
        // 今読んでいる最中の行
        Line line;
        String body;
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

        private final LineReader reader_;
        private final LineReaderHandler lineReaderHandler_;

        private boolean closed_;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);
        private final Line line_ = new LineImpl();
        private final ElementParserContext parserContext_;

        CharacterReadable(final Readable readable,
                final LineReaderHandler lineReaderHandler,
                final ElementParserContext parserContext) {
            reader_ = new LineReadable(readable);
            lineReaderHandler_ = lineReaderHandler;
            parserContext_ = parserContext;
        }

        public Line readLine() throws IOException {
            while (true) {
                final Line line = lineReaderHandler_.readLine(reader_, line_);
                if (line == null) {
                    return null;
                }
                if (lineReaderHandler_.acceptLine(line, parserContext_)) {
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

    }

    public enum RecordState {
        VALID, INVALID
    }

}
