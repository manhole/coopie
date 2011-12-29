package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public class DefaultLineReaderHandler implements LineReaderHandler {

    private static final LineReaderHandler INSTANCE = new DefaultLineReaderHandler();

    public static LineReaderHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean acceptLine(final Line line,
            final ElementParserContext parserContext) {
        return true;
    }

    @Override
    public Line readLine(final LineReader lineReader, final Line reusableLine)
            throws IOException {
        final Line line = lineReader.readLine(reusableLine);
        return line;
    }

}
