package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public class DefaultReaderHandler implements ElementReaderHandler,
        LineReaderHandler, ElementEditor {

    private static final DefaultReaderHandler INSTANCE = new DefaultReaderHandler();

    public static DefaultReaderHandler getInstance() {
        return INSTANCE;
    }

    private final LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();
    private final ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler
            .getInstance();
    private final ElementEditor elementEditor_ = ElementEditors.passThrough();

    @Override
    public boolean acceptLine(final Line line,
            final ElementParserContext parserContext) {
        return lineReaderHandler_.acceptLine(line, parserContext);
    }

    @Override
    public Line readLine(final LineReader lineReader, final Line reusableLine)
            throws IOException {
        return lineReaderHandler_.readLine(lineReader, reusableLine);
    }

    @Override
    public String[] readRecord(final ElementReader elementReader) {
        return elementReaderHandler_.readRecord(elementReader);
    }

    @Override
    public String edit(final String element) {
        return elementEditor_.edit(element);
    }

}
