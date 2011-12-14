package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;

public class FilterLineReader implements LineReader {

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

}
