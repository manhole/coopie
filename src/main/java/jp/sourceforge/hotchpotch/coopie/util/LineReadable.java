package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;

public class LineReadable implements Closable {

    private static final char CR = IOUtil.CR;
    private static final char LF = IOUtil.LF;
    private int lineNumber_;

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);
    private final BufferedReadable br_;

    public LineReadable(final Readable readable) {
        br_ = new BufferedReadable(readable);
    }

    public Line readLine() throws IOException {
        if (br_.isEof()) {
            return null;
        }

        final StringBuilder sb = new StringBuilder();
        LineSeparator sep = LineSeparator.NONE;
        for (char c = br_.readChar(); !br_.isEof(); c = br_.readChar()) {
            if (c == CR) {
                if (br_.peekChar() == LF) {
                    br_.readChar();
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

        if (br_.isEof() && sb.length() == 0) {
            return null;
        }

        final Line line = new LineImpl(sb.toString(), lineNumber_, sep);
        lineNumber_++;
        return line;
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        CloseableUtil.closeNoException(br_);
    }

    /**
     * 初期値は"0"。
     * 1行目を読み終えたら"1"。
     */
    public int getLineNumber() {
        return lineNumber_;
    }

}
