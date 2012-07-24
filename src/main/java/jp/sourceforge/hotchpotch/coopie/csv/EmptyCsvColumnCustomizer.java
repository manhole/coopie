package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collection;

public class EmptyCsvColumnCustomizer implements CsvColumnCustomizer {

    private static EmptyCsvColumnCustomizer INSTANCE = new EmptyCsvColumnCustomizer();

    public static EmptyCsvColumnCustomizer getInstance() {
        return INSTANCE;
    }

    @Override
    public void customize(final Collection<? extends CsvColumnDef> columnDef) {
        // no op.
    }

}
