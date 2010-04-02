package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;

public class BeanColumnLayout<T> {

    private BeanColumnDesc<T>[] columnDescs;
    // 一時的
    private ColumnNames columnNames;
    private final BeanDesc<T> beanDesc;

    public BeanColumnLayout(final Class<T> beanClass) {
        this.beanDesc = BeanDescFactory.getBeanDesc(beanClass);
    }

    public BeanDesc<T> getBeanDesc() {
        return beanDesc;
    }

    private BeanColumnDesc<T>[] getColumnDescs() {
        if (columnDescs == null) {
            /*
             * beanの全プロパティを対象に。
             */
            if (columnNames == null || columnNames.isEmpty()) {
                final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
                columnDescs = newBeanColumnDescs(pds.size());
                int i = 0;
                for (final PropertyDesc<T> pd : pds) {
                    final String propertyName = pd.getPropertyName();
                    final ColumnName name = new SimpleColumnName(propertyName);
                    final BeanColumnDesc<T> cd = newBeanColumnDesc(name, pd);
                    columnDescs[i] = cd;
                    i++;
                }
            } else {
                final ColumnName[] names = columnNames.getColumnNames();
                columnDescs = newBeanColumnDescs(names.length);
                int i = 0;
                for (final ColumnName columnName : names) {
                    final PropertyDesc<T> pd = getPropertyDesc(beanDesc,
                        columnName.getName());
                    final BeanColumnDesc<T> cd = newBeanColumnDesc(columnName,
                        pd);
                    columnDescs[i] = cd;
                    i++;
                }
            }
        }
        return columnDescs;
    }

    private BeanColumnDesc<T> newBeanColumnDesc(final ColumnName name,
        final PropertyDesc<T> pd) {
        final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
        cd.setPropertyDesc(pd);
        cd.setName(name);
        return cd;
    }

    @SuppressWarnings("unchecked")
    private BeanColumnDesc<T>[] newBeanColumnDescs(final int length) {
        return new BeanColumnDesc[length];
    }

    public ColumnName[] getNames() {
        final ColumnDesc<T>[] cds = getColumnDescs();
        final ColumnName[] names = new ColumnName[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<T> cd = cds[i];
            names[i] = cd.getName();
        }
        return names;
    }

    private void setColumns(final String... names) {
        final ColumnName[] columns = new ColumnName[names.length];
        for (int i = 0; i < names.length; i++) {
            final String name = names[i];
            final ColumnName columnName = new SimpleColumnName(name);
            columns[i] = columnName;
        }
        setColumns(columns);
    }

    private void setColumns(final ColumnName... columns) {
        final ColumnNames csvColumns = new ColumnNames();
        for (final ColumnName columnName : columns) {
            csvColumns.add(columnName);
        }
        setColumns(csvColumns);
    }

    public void setColumns(final ColumnNames columns) {
        columnNames = columns;
        columnDescs = null;
    }

    public String[] getValues(final T bean) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        final String[] line = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<T> cd = cds[i];
            final String v = cd.getValue(bean);
            line[i] = v;
        }
        return line;
    }

    public void setValues(final T bean, final String[] line) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        for (int i = 0; i < line.length; i++) {
            final String elem = line[i];
            final ColumnDesc<T> cd = cds[i];
            cd.setValue(bean, elem);
        }
    }

    public void setupByHeader(final String[] header) {
        /*
         * 既にColumnDescが設定されている場合は、
         * ヘッダの順序に合わせてソートし直す。
         * 
         * CSVヘッダ名を別名として扱う。
         */
        final BeanColumnDesc<T>[] tmpCds = getColumnDescs();
        final BeanColumnDesc<T>[] cds = newBeanColumnDescs(tmpCds.length);

        int i = 0;
        HEADER: for (final String headerElem : header) {
            for (final BeanColumnDesc<T> cd : tmpCds) {
                final ColumnName name = cd.getName();
                if (name.getLabel().equals(headerElem)) {
                    cds[i] = cd;
                    i++;
                    continue HEADER;
                }
            }
            // TODO
            throw new RuntimeException("headerElem=" + headerElem);
        }
        columnDescs = cds;
    }

    private PropertyDesc<T> getPropertyDesc(final BeanDesc<T> beanDesc,
        final String name) {
        final PropertyDesc<T> pd = beanDesc.getPropertyDesc(name);
        if (pd == null) {
            // TODO
            throw new RuntimeException(name);
        }
        return pd;
    }

    public void setupColumns(final ColumnSetup columnSetup) {
        final ColumnNames columns = new ColumnNames();
        columns.setupColumns(columnSetup);
        setColumns(columns);
    }

    public static abstract class ColumnSetup {

        public abstract void setup();

        private final List<ColumnName> columnNames = CollectionsUtil
            .newArrayList();

        protected final void column(final ColumnName name) {
            columnNames.add(name);
        }

        protected final void column(final String name) {
            column(new SimpleColumnName(name));
        }

        protected final void column(final String propertyName,
            final String label) {

            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            column(n);
        }

    }

    public static class ColumnNames {

        private final List<ColumnName> columnNames = CollectionsUtil
            .newArrayList();

        public void setupColumns(final ColumnSetup columnSetup) {
            columnSetup.setup();
            final List<ColumnName> names = columnSetup.columnNames;
            columnNames.clear();
            columnNames.addAll(names);
        }

        public void add(final ColumnName columnName) {
            columnNames.add(columnName);
        }

        public ColumnName[] getColumnNames() {
            return columnNames.toArray(new ColumnName[columnNames.size()]);
        }

        public boolean isEmpty() {
            return columnNames.isEmpty();
        }

    }

}
