package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Closeable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.IOUtil;
import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

/**
 * CSV Reader
 * 
 * http://www.rfc-editor.org/rfc/rfc4180.txt
 * http://www.kasai.fm/wiki/rfc4180jp (日本語訳)
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
    private BufferedReadable br_;

    private char elementSeparator_ = CsvSetting.COMMA;
    private char quoteMark_ = CsvSetting.DOUBLE_QUOTE;

    public void open(final Readable readable) {
        br_ = new BufferedReadable(readable);
        closed_ = false;
    }

    @Override
    public int getRecordNo() {
        return recordNo_;
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
        return br_.peekChar();
    }

    private char next() throws IOException {
        return br_.readChar();
    }

    /*
     * EOF
     */
    private boolean isEof() {
        return br_.isEof();
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        IOUtil.closeNoException(br_);
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
            if (inRecord_) {
                throw new IllegalStateException("already started");
            }
            inRecord_ = true;
        }

        public void endRecord() {
            if (!inRecord_) {
                throw new IllegalStateException("already ended");
            }
            inRecord_ = false;
        }

        public void startElement() {
            if (inElement_) {
                throw new IllegalStateException("already started");
            }
            inElement_ = true;
        }

        public void endElement() {
            if (!isInElement()) {
                throw new IllegalStateException("not started");
            }
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
            if (!isInElement()) {
                throw new IllegalStateException("not started");
            }

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
            if (inRecord_) {
                throw new IllegalStateException("not ended");
            }
            return elems_.toArray(new String[elems_.size()]);
        }

    }

    static enum State {
        INITIAL, BEGIN_ELEMENT, UNQUOTED_ELEMENT, QUOTED_ELEMENT, QUOTE
    }

    static class BufferedReadable implements Closeable {

        private static final char NULL_CHAR = '\u0000';
        private final Readable readable_;
        private final CharBuffer charBuffer_;
        private int readSize_;
        private boolean eof_;

        public BufferedReadable(final Readable readable) {
            this(readable, 128);
        }

        public BufferedReadable(final Readable readable, final int bufferSize) {
            readable_ = readable;
            charBuffer_ = CharBuffer.allocate(bufferSize);
        }

        /**
         * 次の1文字を読み、ポインタを次へ進めます。
         * 次の文字が無い場合(最後まで読んだ場合)は、NULL_CHAR ('\u0000')を返します。
         * 
         * 当メソッドを続けて呼ぶと、1文字ずつ次を返します。
         * 
         * @return 次の1文字
         */
        public char readChar() throws IOException {
            if (isEof()) {
                return NULL_CHAR;
            }

            // bufferの最後まで使った or 初回
            if (charBuffer_.position() == readSize_) {
                charBuffer_.clear();
                readSize_ = readable_.read(charBuffer_);
                charBuffer_.clear();
            }
            if (readSize_ == -1) {
                eof_ = true;
                return NULL_CHAR;
            }

            // ポインタを次へ進める
            final char c = charBuffer_.get();
            return c;
        }

        /**
         * 次の1文字を返します。
         * 次の文字が無い場合(最後まで読んだ場合)は、NULL_CHAR ('\u0000')を返します。
         * 
         * {@link #readChar()}と異なり、ポインタを先へ進めません。
         * 
         * 当メソッドを続けて呼ぶと、同じ文字を返し続けます。
         * 
         * @return 次の1文字
         */
        public char peekChar() throws IOException {
            if (isEof()) {
                return NULL_CHAR;
            }

            if (charBuffer_.position() == readSize_) {
                /*
                 * bufferから既にgetした分を捨てて、残りを左へ詰める
                 */
                {
                    int left = 0;
                    int pos = charBuffer_.position();
                    for (; pos < readSize_; left++, pos++) {
                        final char c = charBuffer_.get();
                        charBuffer_.put(left, c);
                    }
                    charBuffer_.position(left);
                    readSize_ = readable_.read(charBuffer_) + left;
                }
                if (readSize_ == -1) {
                    eof_ = true;
                    return NULL_CHAR;
                }
                charBuffer_.clear();
            }

            // ポインタを次へ進めない
            final char c = charBuffer_.get(charBuffer_.position());
            return c;
        }

        public boolean isEof() {
            return eof_;
        }

        @Override
        public void close() throws IOException {
            if (readable_ instanceof Closeable) {
                final Closeable closeable = Closeable.class.cast(readable_);
                IOUtil.closeNoException(closeable);
            }
        }

    }

}