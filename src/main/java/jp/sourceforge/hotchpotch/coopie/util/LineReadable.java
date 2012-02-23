package jp.sourceforge.hotchpotch.coopie.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Deque;
import java.util.LinkedList;

public class LineReadable implements LineReader {

    private static final char CR = IOUtil.CR;
    private static final char LF = IOUtil.LF;

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);
    private final Readable readable_;
    private LineSeparator lineSeparator_;
    private String line_;
    private int lineNumber_;
    private Deque<Line> pushback_;
    private boolean eof_;
    private char[] buffer_ = new char[0];
    private int pos_;
    private int length_;
    private final CharBuffer charBuffer_;

    public LineReadable(final Readable readable) {
        readable_ = readable;
        charBuffer_ = CharBuffer.allocate(1024 * 8);
    }

    public String readLineBody() throws IOException {
        read0();
        return line_;
    }

    @Override
    public Line readLine() throws IOException {
        final Line line = createLine();
        final Line read = readLine(line);
        return read;
    }

    @Override
    public Line readLine(final Line reusableLine) throws IOException {
        read0();
        if (line_ == null) {
            return null;
        }

        reusableLine.reinit(line_, lineNumber_, lineSeparator_);
        return reusableLine;
    }

    protected Line createLine() {
        return new LineImpl();
    }

    private void read0() throws IOException {
        if (pushback_ != null) {
            final Line line = pushback_.pop();
            line_ = line.getBody();
            lineNumber_++;
            lineSeparator_ = line.getSeparator();
            if (pushback_.isEmpty()) {
                pushback_ = null;
            }
            return;
        }

        if (eof_) {
            line_ = null;
            lineSeparator_ = null;
            return;
        }

        StringBuilder bodyBuff = null;
        int bodyStartPos = pos_;
        int bodyLength = 0;
        LineSeparator sep = LineSeparator.NONE;
        read_loop: while (true) {
            if (length_ <= pos_) {
                if (0 < bodyLength) {
                    if (bodyBuff == null) {
                        bodyBuff = new StringBuilder();
                    }
                    bodyBuff.append(buffer_, bodyStartPos, bodyLength);
                }
                fill();
                if (eof_) {
                    break read_loop;
                }
                bodyStartPos = pos_;
                bodyLength = 0;
            }

            for (; pos_ < length_;) {
                final char c = buffer_[pos_];
                pos_++;
                if (c == CR) {
                    if (length_ <= pos_) {
                        if (0 < bodyLength) {
                            if (bodyBuff == null) {
                                bodyBuff = new StringBuilder();
                            }
                            bodyBuff.append(buffer_, bodyStartPos, bodyLength);
                        }
                        fill();
                        if (eof_) {
                            sep = LineSeparator.CR;
                            break read_loop;
                        }
                        bodyStartPos = pos_;
                        bodyLength = 0;
                    }
                    if (buffer_[pos_] == LF) {
                        sep = LineSeparator.CRLF;
                        pos_++;
                    } else {
                        sep = LineSeparator.CR;
                    }
                    break read_loop;
                } else if (c == LF) {
                    sep = LineSeparator.LF;
                    break read_loop;
                } else {
                    bodyLength++;
                }
            }
        }

        if (eof_ && bodyBuff == null && bodyLength == 0) {
            line_ = null;
            lineSeparator_ = null;
            return;
        }

        if (bodyBuff == null) {
            line_ = new String(buffer_, bodyStartPos, bodyLength);
        } else {
            if (eof_) {
            } else {
                bodyBuff.append(buffer_, bodyStartPos, bodyLength);
            }
            line_ = bodyBuff.toString();
        }

        lineNumber_++;
        lineSeparator_ = sep;
    }

    protected void fill() throws IOException {
        final int len = readable_.read(charBuffer_);
        final char[] chars = charBuffer_.array();
        charBuffer_.rewind();
        if (len == -1) {
            eof_ = true;
        } else {
            pos_ = 0;
            buffer_ = chars;
            length_ = len;
        }
    }

    public void pushback(final Line line) {
        if (pushback_ == null) {
            pushback_ = new LinkedList<Line>();
        }
        pushback_.push(line);
        lineNumber_--;
        // XXX この時点では手前の行のことは忘れているため、わからない
        line_ = null;
        lineSeparator_ = null;
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (readable_ instanceof Closeable) {
            final Closeable closeable = Closeable.class.cast(readable_);
            CloseableUtil.closeNoException(closeable);
        }
    }

    /**
     * 初期値は"0"。
     * 1行目を読み終えたら"1"。
     */
    @Override
    public int getLineNumber() {
        return lineNumber_;
    }

    public LineSeparator getLineSeparator() {
        return lineSeparator_;
    }

}
