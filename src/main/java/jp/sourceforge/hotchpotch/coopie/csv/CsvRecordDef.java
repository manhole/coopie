package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

public interface CsvRecordDef {

    List<? extends CsvColumnDef> getColumnDefs();

    void addColumnDef(CsvColumnDef columnDef);

    List<? extends CsvColumnsDef> getColumnsDefs();

    void addColumnsDef(CsvColumnsDef columnsDef);

    OrderSpecified getOrderSpecified();

    void setOrderSpecified(OrderSpecified orderSpecified);

}
