package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;

public class LineReadable implements LineReader {

    private static final char CR = IOUtil.CR;
    private static final char LF = IOUtil.LF;

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);
    private final BufferedReadable readable_;
    private LineSeparator lineSeparator_;
    private String line_;
    private int lineNumber_;
    private final Deque<Line> buf_ = new LinkedList<Line>();

    public LineReadable(final Readable readable) {
        readable_ = new BufferedReadable(readable);
    }

    public String readLineBody() throws IOException {
        read0();
        return line_;
    }

    @Override
    public Line readLine() throws IOException {
        final Line line = createLine();
        final boolean read = readLine(line);
        if (read) {
            return line;
        }
        return null;
    }

    public boolean readLine(final Line line) throws IOException {
        read0();
        if (line_ == null) {
            return false;
        }

        line.reinit(line_, lineNumber_, lineSeparator_);
        return true;
    }

    protected Line createLine() {
        return new LineImpl();
    }

    private void read0() throws IOException {
        if (!buf_.isEmpty()) {
            final Line line = buf_.pop();
            line_ = line.getBody();
            lineNumber_++;
            lineSeparator_ = line.getSeparator();
            return;
        }

        if (readable_.isEof()) {
            line_ = null;
            lineSeparator_ = null;
            return;
        }

        final StringBuilder sb = new StringBuilder();
        LineSeparator sep = LineSeparator.NONE;
        for (char c = readable_.readChar(); !readable_.isEof(); c = readable_
                .readChar()) {
            if (c == CR) {
                if (readable_.peekChar() == LF) {
                    readable_.readChar();
                    sep = LineSeparator.CRLF;
                } else {
                    sep = LineSeparator.CR;
                }
                break;
            } else if (c == LF) {
                sep = LineSeparator.LF;
                break;
            } else {
                sb.append(c);
            }
        }

        if (readable_.isEof() && sb.length() == 0) {
            line_ = null;
            lineSeparator_ = null;
            return;
        }

        line_ = sb.toString();
        lineNumber_++;
        lineSeparator_ = sep;
    }

    public void pushback(final Line line) {
        buf_.push(line);
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
        CloseableUtil.closeNoException(readable_);
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
