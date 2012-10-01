package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collection;

public interface CsvColumnCustomizer {

    void customize(Collection<? extends CsvColumnDef> columnDef);

}
