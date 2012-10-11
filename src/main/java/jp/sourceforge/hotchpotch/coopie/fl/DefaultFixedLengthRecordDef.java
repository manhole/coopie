package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

class DefaultFixedLengthRecordDef implements FixedLengthRecordDef {

    final List<FixedLengthColumnDef> columnDefs_ = CollectionsUtil
            .newArrayList();
    private final List<FixedLengthColumnsDef> columnsDefs_ = CollectionsUtil
            .newArrayList();

    public boolean isEmpty() {
        return getColumnDefs().isEmpty();
    }

    @Override
    public List<? extends FixedLengthColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    @Override
    public void addColumnDef(final FixedLengthColumnDef columnDef) {
        columnDefs_.add(columnDef);
    }

    @Override
    public List<? extends FixedLengthColumnsDef> getColumnsDefs() {
        return columnsDefs_;
    }

    @Override
    public void addColumnsDef(final FixedLengthColumnsDef columnsDef) {
        columnsDefs_.add(columnsDef);
    }

    @Override
    public List<? extends FixedLengthColumnDef> getAllColumnDefs() {
        final List<FixedLengthColumnDef> all = CollectionsUtil.newArrayList();
        all.addAll(getColumnDefs());
        final List<? extends FixedLengthColumnsDef> columnsDefs = getColumnsDefs();
        for (final FixedLengthColumnsDef columnsDef : columnsDefs) {
            final List<FixedLengthColumnDef> columnDefs = columnsDef
                    .getColumnDefs();
            all.addAll(columnDefs);
        }
        return all;
    }

}
