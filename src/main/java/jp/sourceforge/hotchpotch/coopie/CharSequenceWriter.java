package jp.sourceforge.hotchpotch.coopie;

import java.io.IOException;
import java.io.Writer;

public class CharSequenceWriter extends Writer {

    private static final String LINE_SEPARATOR = System
        .getProperty("line.separator");

    private final StringBuilder buf;
    private String lineSeparator = LINE_SEPARATOR;

    public CharSequenceWriter() {
        buf = new StringBuilder();
        lock = buf;
    }

    @Override
    public void write(final int c) {
        buf.append((char) c);
    }

    @Override
    public void write(final char cbuf[], final int off, final int len) {
        if ((off < 0) || (off > cbuf.length) || (len < 0)
            || ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        buf.append(cbuf, off, len);
    }

    @Override
    public void write(final String str) {
        buf.append(str);
    }

    @Override
    public void write(final String str, final int off, final int len) {
        buf.append(str.substring(off, off + len));
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
        final CharSequence cs = (csq == null ? "null" : csq);
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
        return buf.toString();
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
    }

    public StringBuilder getBuffer() {
        return buf;
    }

    public void writeLine(final CharSequence csq) {
        append(csq);
        newLine();
    }

    public void newLine() {
        append(lineSeparator);
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

}
