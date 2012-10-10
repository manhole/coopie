package jp.sourceforge.hotchpotch.coopie.csv;

public class EmptyRecordDefCustomizer implements CsvRecordDefCustomizer {

    private static EmptyRecordDefCustomizer INSTANCE = new EmptyRecordDefCustomizer();

    public static EmptyRecordDefCustomizer getInstance() {
        return INSTANCE;
    }

    @Override
    public void customize(final CsvRecordDef recordDef) {
        // no op.
    }

}
