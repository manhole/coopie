package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public class DefaultReadEditor implements ReadEditor {

    private static final DefaultReadEditor INSTANCE = new DefaultReadEditor();

    public static ReadEditor getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean acceptLine(final Line line,
            final ElementParserContext parserContext) {
        return true;
    }

    @Override
    public Line readLine(final LineReader lineReader) throws IOException {
        return lineReader.readLine();
    }

    @Override
    public String[] readRecord(final ElementReader elementReader) {
        return elementReader.readRecord();
    }

}
