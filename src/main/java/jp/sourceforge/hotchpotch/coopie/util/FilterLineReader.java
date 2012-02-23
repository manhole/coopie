package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;

public class FilterLineReader implements LineReader {

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private final LineReader lineReader_;
    private final LineFilter lineFilter_;

    public FilterLineReader(final LineReader lineReader,
            final LineFilter lineFilter) {
        lineReader_ = lineReader;
        lineFilter_ = lineFilter;
    }

    @Override
    public int getLineNumber() {
        return lineReader_.getLineNumber();
    }

    @Override
    public Line readLine() throws IOException {
        while (true) {
            final Line line = lineReader_.readLine();
            if (line == null) {
                return null;
            }
            if (lineFilter_.accept(line)) {
                return line;
            }
        }
    }

    @Override
    public Line readLine(final Line reusableLine) throws IOException {
        Line line = reusableLine;
        while (true) {
            line = lineReader_.readLine(line);
            if (line == null) {
                return null;
            }
            if (lineFilter_.accept(line)) {
                return line;
            }
        }
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        CloseableUtil.closeNoException(lineReader_);
    }

}
