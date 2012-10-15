package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.util.CollectionsUtil;

class DefaultCsvRecordDef implements CsvRecordDef {

    private final List<CsvColumnDef> columnDefs_ = CollectionsUtil
            .newArrayList();
    private final List<CsvColumnsDef> columnsDefs_ = CollectionsUtil
            .newArrayList();
    private OrderSpecified orderSpecified_ = OrderSpecified.NO;

    public boolean isEmpty() {
        return getColumnDefs().isEmpty() && getColumnsDefs().isEmpty();
    }

    @Override
    public void addColumnDef(final CsvColumnDef columnDef) {
        columnDefs_.add(columnDef);
    }

    @Override
    public List<? extends CsvColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    @Override
    public void addColumnsDef(final CsvColumnsDef columnsDef) {
        columnsDefs_.add(columnsDef);
    }

    @Override
    public List<? extends CsvColumnsDef> getColumnsDefs() {
        return columnsDefs_;
    }

    @Override
    public OrderSpecified getOrderSpecified() {
        return orderSpecified_;
    }

    @Override
    public void setOrderSpecified(final OrderSpecified orderSpecified) {
        orderSpecified_ = orderSpecified;
    }

    @Override
    public List<? extends CsvColumnDef> getAllColumnDefs() {
        final List<CsvColumnDef> all = CollectionsUtil.newArrayList();
        all.addAll(getColumnDefs());
        final List<? extends CsvColumnsDef> columnsDefs = getColumnsDefs();
        for (final CsvColumnsDef columnsDef : columnsDefs) {
            final List<CsvColumnDef> columnDefs = columnsDef.getColumnDefs();
            all.addAll(columnDefs);
        }
        return all;
    }

}
