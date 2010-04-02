package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collections;
import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

class ColumnNames {

    private final List<ColumnName> columnNames = CollectionsUtil.newArrayList();

    public void setupColumns(final ColumnSetup columnSetup) {
        columnSetup.setup();
        final List<ColumnName> names = columnSetup.columnNames;
        columnNames.clear();
        columnNames.addAll(names);
    }

    public void add(final ColumnName columnName) {
        columnNames.add(columnName);
    }

    public void addAll(final ColumnName... names) {
        Collections.addAll(columnNames, names);
    }

    public ColumnName[] getColumnNames() {
        return columnNames.toArray(new ColumnName[columnNames.size()]);
    }

    public boolean isEmpty() {
        return columnNames.isEmpty();
    }

}
