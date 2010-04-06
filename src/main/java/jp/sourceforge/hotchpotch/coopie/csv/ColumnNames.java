package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

class ColumnNames {

    private final List<ColumnName> columnNames = CollectionsUtil.newArrayList();

    public void add(final ColumnName columnName) {
        columnNames.add(columnName);
    }

    public void addAll(final ColumnName... names) {
        Collections.addAll(columnNames, names);
    }

    public void addAll(final Collection<ColumnName> names) {
        columnNames.addAll(names);
    }

    public ColumnName[] getColumnNames() {
        return columnNames.toArray(new ColumnName[columnNames.size()]);
    }

    public boolean isEmpty() {
        return columnNames.isEmpty();
    }

}
