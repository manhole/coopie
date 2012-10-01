package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

public interface RecordDef {

    List<? extends CsvColumnDef> getColumnDefs();

    void addColumnDef(CsvColumnDef columnDef);

    List<? extends CsvColumnsDef> getColumnsDefs();

    void addColumnsDef(CsvColumnsDef columnsDef);

}
