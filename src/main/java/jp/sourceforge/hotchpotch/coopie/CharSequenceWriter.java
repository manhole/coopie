package jp.sourceforge.hotchpotch.coopie;

import java.io.IOException;
import java.io.Writer;

public class CharSequenceWriter extends Writer {

    private final StringBuilder buf_;
    private String lineSeparator_ = IOUtil.getSystemLineSeparator();

    public CharSequenceWriter() {
        buf_ = new StringBuilder();
        lock = buf_;
    }

    @Override
    public void write(final int c) {
        buf_.append((char) c);
    }

    @Override
    public void write(final char cbuf[], final int off, final int len) {
        if (off < 0 || off > cbuf.length || len < 0 || off + len > cbuf.length
                || off + len < 0) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        buf_.append(cbuf, off, len);
    }

    @Override
    public void write(final String str) {
        buf_.append(str);
    }

    @Override
    public void write(final String str, final int off, final int len) {
        buf_.append(str.substring(off, off + len));
    }

    @Override
    public CharSequenceWriter append(final CharSequence csq) {
        if (csq == null) {
            write("null");
        } else {
            write(csq.toString());
        }
        return this;
    }

    @Override
    public CharSequenceWriter append(final CharSequence csq, final int start,
            final int end) {
        final CharSequence cs = csq == null ? "null" : csq;
        write(cs.subSequence(start, end).toString());
        return this;
    }

    @Override
    public CharSequenceWriter append(final char c) {
        write(c);
        return this;
    }

    @Override
    public String toString() {
        return buf_.toString();
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
    }

    public StringBuilder getBuffer() {
        return buf_;
    }

    public void writeLine(final CharSequence csq) {
        append(csq);
        newLine();
    }

    public void newLine() {
        append(lineSeparator_);
    }

    public String getLineSeparator() {
        return lineSeparator_;
    }

    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

}
