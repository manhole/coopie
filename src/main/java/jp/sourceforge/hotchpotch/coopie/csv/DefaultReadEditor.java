package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultReadEditor implements ReadEditor {

    private static final DefaultReadEditor INSTANCE = new DefaultReadEditor();

    public static ReadEditor getInstance() {
        return INSTANCE;
    }

    @Override
    public String[] readRecord(final ElementReader elementReader) {
        return elementReader.readRecord();
    }

}
