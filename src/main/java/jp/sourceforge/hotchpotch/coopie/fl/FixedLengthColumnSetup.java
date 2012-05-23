package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup;

public interface FixedLengthColumnSetup {

    CsvColumnSetup.ColumnBuilder column(FixedLengthColumnDef columnDef);

    CsvColumnSetup.ColumnBuilder column(String name, int beginIndex,
            int endIndex);

    CsvColumnSetup.ColumnBuilder columns(FixedLengthColumnDef... columnDefs);

    // 複数カラムをプロパティと対応づける際に使用する
    FixedLengthColumnDef c(String name, int beginIndex, int endIndex);

}
