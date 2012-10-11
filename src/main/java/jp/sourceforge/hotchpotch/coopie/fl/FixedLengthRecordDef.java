package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

public interface FixedLengthRecordDef {

    List<? extends FixedLengthColumnDef> getColumnDefs();

    void addColumnDef(FixedLengthColumnDef columnDef);

    List<? extends FixedLengthColumnsDef> getColumnsDefs();

    void addColumnsDef(FixedLengthColumnsDef columnsDef);

    /*
     * Compositも含む
     */
    List<? extends FixedLengthColumnDef> getAllColumnDefs();

}
