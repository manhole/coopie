package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

public interface RecordDef {

    void addColumnDef(CsvColumnDef columnDef);

    List<? extends CsvColumnDef> getColumnDefs();

}
