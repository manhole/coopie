package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;

public interface FixedLengthColumnSetup {

    CsvColumnSetup.ColumnBuilder column(FixedLengthColumnDef columnDef);

    CsvColumnSetup.ColumnBuilder column(String name, int beginIndex,
            int endIndex);

    CsvColumnSetup.CompositeColumnBuilder columns(
            final SetupBlock<FixedLengthCompositeColumnSetup> compositeSetup);

    // 複数カラムをプロパティと対応づける際に使用する
    FixedLengthColumnDef c(String name, int beginIndex, int endIndex);

    public interface FixedLengthCompositeColumnSetup {

        CsvColumnSetup.ColumnBuilder column(FixedLengthColumnDef columnDef);

        CsvColumnSetup.ColumnBuilder column(String name, int beginIndex,
                int endIndex);

    }

}
