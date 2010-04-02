package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

public abstract class ColumnSetup {

    public abstract void setup();

    final List<ColumnName> columnNames = CollectionsUtil.newArrayList();

    protected final void column(final ColumnName name) {
        columnNames.add(name);
    }

    protected final void column(final String name) {
        column(new SimpleColumnName(name));
    }

    protected final void column(final String propertyName, final String label) {
        final SimpleColumnName n = new SimpleColumnName();
        n.setName(propertyName);
        n.setLabel(label);
        column(n);
    }

}
