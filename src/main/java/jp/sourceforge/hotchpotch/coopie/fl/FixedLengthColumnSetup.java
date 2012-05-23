package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup;

public interface FixedLengthColumnSetup {

    CsvColumnSetup.ColumnBuilder column(FixedLengthColumnDef columnDef);

    CsvColumnSetup.ColumnBuilder column(String propertyName, int beginIndex,
            int endIndex);

}
