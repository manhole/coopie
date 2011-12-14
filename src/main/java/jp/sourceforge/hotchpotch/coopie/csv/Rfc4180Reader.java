package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.Closable;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
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

    protected boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private int recordNo_;
    private CharacterReadable reader_;

    private char elementSeparator_ = CsvSetting.COMMA;
    private char quoteMark_ = CsvSetting.DOUBLE_QUOTE;

    public void open(final Readable readable) {
        reader_ = new CharacterReadable(readable);
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

    @Override
    public String[] readRecord() {
        if (isEof()) {
            return null;
        }

        final RecordBuffer rb = new RecordBuffer();
        State state = State.INITIAL;
        try {
            read_loop: for (char c = next(); !isEof(); c = next()) {
                switch (state) {
                case INITIAL:
                    if (c == quoteMark_) {
                        state = State.QUOTED_ELEMENT;
                        rb.startRecord();
                        rb.startElement();
                    } else if (c == elementSeparator_) {
                        state = State.BEGIN_ELEMENT;
                        rb.startRecord();
                        rb.startElement();
                        rb.endElement();
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        break read_loop;
                    } else if (c == LF) {
                        break read_loop;
                    } else {
                        state = State.UNQUOTED_ELEMENT;
                        rb.startRecord();
                        rb.startElement();
                        rb.append(c);
                    }
                    break;

                case BEGIN_ELEMENT:
                    if (c == quoteMark_) {
                        state = State.QUOTED_ELEMENT;
                        rb.startElement();
                    } else if (c == elementSeparator_) {
                        rb.startElement();
                        rb.endElement();
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        rb.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else if (c == LF) {
                        rb.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else {
                        state = State.UNQUOTED_ELEMENT;
                        rb.startElement();
                        rb.append(c);
                    }
                    break;

                case UNQUOTED_ELEMENT:
                    if (c == quoteMark_) {
                        rb.append(c);
                    } else if (c == elementSeparator_) {
                        rb.endElement();
                        state = State.BEGIN_ELEMENT;
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        rb.endElement();
                        rb.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else if (c == LF) {
                        rb.endElement();
                        rb.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else {
                        rb.append(c);
                    }
                    break;

                case QUOTED_ELEMENT:
                    if (c == quoteMark_) {
                        state = State.QUOTE;
                    } else {
                        rb.append(c);
                    }
                    break;

                case QUOTE:
                    if (c == quoteMark_) {
                        rb.append(c);
                        state = State.QUOTED_ELEMENT;
                    } else if (c == elementSeparator_) {
                        rb.endElement();
                        state = State.BEGIN_ELEMENT;
                    } else if (c == CR) {
                        consumeFollowLfIfPossible();
                        rb.endElement();
                        rb.endRecord();
                        state = State.INITIAL;
                        break read_loop;
                    } else if (c == LF) {
                        rb.endElement();
                        rb.endRecord();
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
                        logger.warn("invalid record: recordNo={}, char={}",
                                recordNo_, c);
                        /*
                         * ここでは、エスケープ文字と続く不正文字を、通常の文字として扱うことにする。
                         * つまり、"abc"d" このような入力を、"abc""d" とみなすということ。
                         * (見直す可能性アリ)
                         */
                        rb.append(quoteMark_);
                        rb.append(c);
                        state = State.QUOTED_ELEMENT;
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
                rb.startElement();
                rb.endElement();
                rb.endRecord();
                break;
            case UNQUOTED_ELEMENT:
                if (rb.isInElement()) {
                    rb.endElement();
                }
                rb.endRecord();
                break;
            case QUOTED_ELEMENT:
                // クォート文字しかないrecordの場合
                if (rb.isEmpty()) {
                    rb.append(quoteMark_);
                    rb.endElement();
                }
                // クォートされた要素の途中でEOFになった場合
                if (rb.isInElement()) {
                    rb.endElement();
                }
                rb.endRecord();
                break;
            case QUOTE:
                if (rb.isInElement()) {
                    rb.endElement();
                }
                rb.endRecord();
                break;
            default:
                throw new AssertionError();
            }

            if (isEof() && rb.isEmpty()) {
                return null;
            }

            recordNo_++;
            final String[] record = rb.toRecord();
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
        return reader_.peekChar();
    }

    private char next() throws IOException {
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

    static class RecordBuffer {

        private final List<String> elems_ = CollectionsUtil.newArrayList();
        private final StringBuilder sb_ = new StringBuilder();
        private boolean inElement_;
        private boolean inRecord_;

        public void startRecord() {
            assertNotInRecord();
            inRecord_ = true;
        }

        public void endRecord() {
            assertInRecord();
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
            elems_.add(bufferString());
            inElement_ = false;
        }

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

        private String bufferString() {
            final String s = sb_.toString();
            sb_.setLength(0);
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
        private final LineReadable reader_;
        private char[] chars_ = new char[] {};
        private int pos_;
        private boolean eof_;

        private boolean closed_;
        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        CharacterReadable(final Readable readable) {
            reader_ = new LineReadable(readable);
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
                final String line = reader_.readLine();
                if (line == null) {
                    eof_ = true;
                    return;
                }
                final LineSeparator sep = reader_.getLineSeparator();
                final String end = sep.getSeparator();
                chars_ = (line + end).toCharArray();
                pos_ = 0;
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

}
