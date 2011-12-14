package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultElementReaderHandler implements ElementReaderHandler {

    private static final ElementReaderHandler INSTANCE = new DefaultElementReaderHandler();

    public static ElementReaderHandler getInstance() {
        return INSTANCE;
    }

    @Override
    public String[] readRecord(final ElementReader elementReader) {
        return elementReader.readRecord();
    }

}
