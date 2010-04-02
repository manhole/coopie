package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;

public class BeanColumnLayout<T> {

    private BeanColumnDesc<T>[] columnDescs;
    private final Map<String, BeanColumnDesc<T>> columnDescsMap = CollectionsUtil
        .newHashMap();
    // 一時的
    private List<ColumnName> columnNames;
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
            if (columnNames == null) {
                final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
                columnDescs = new BeanColumnDesc[pds.size()];
                int i = 0;
                for (final PropertyDesc<T> pd : pds) {
                    final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                    cd.setPropertyDesc(pd);
                    final String propertyName = pd.getPropertyName();
                    cd.setName(new SimpleColumnName(propertyName));
                    columnDescs[i] = cd;
                    columnDescsMap.put(propertyName, cd);
                    i++;
                }
            } else {
                columnDescs = new BeanColumnDesc[columnNames.size()];
                int i = 0;
                for (final ColumnName columnName : columnNames) {
                    final BeanColumnDesc<T> cd = new BeanColumnDesc();
                    cd.setName(columnName);
                    final PropertyDesc<T> pd = getPropertyDesc(beanDesc,
                        columnName.getName());
                    cd.setPropertyDesc(pd);
                    columnDescs[i] = cd;
                    i++;
                }
            }
        }
        return columnDescs;
    }

    public ColumnName[] getNames() {
        final BeanColumnDesc<T>[] cds = getColumnDescs();
        final ColumnName[] names = new ColumnName[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final BeanColumnDesc<T> cd = cds[i];
            names[i] = cd.getName();
        }
        return names;
    }

    public void setColumns(final String... names) {
        final ColumnName[] columns = new ColumnName[names.length];
        for (int i = 0; i < names.length; i++) {
            final String name = names[i];
            final ColumnName columnName = new SimpleColumnName(name);
            columns[i] = columnName;
        }
        setColumns(columns);
    }

    public void setColumns(final ColumnName... columns) {
        this.columnNames = CollectionsUtil.newArrayList();
        Collections.addAll(this.columnNames, columns);
        columnDescs = null;
    }

    public String[] getValues(final T bean) {
        final BeanColumnDesc<T>[] cds = getColumnDescs();
        final String[] line = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final BeanColumnDesc<T> cd = cds[i];
            final String v = cd.getValue(bean);
            line[i] = v;
        }
        return line;
    }

    public void setValues(final T bean, final String[] line) {
        final BeanColumnDesc<T>[] cds = getColumnDescs();
        for (int i = 0; i < line.length; i++) {
            final String elem = line[i];
            final BeanColumnDesc<T> cd = cds[i];
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
        final BeanColumnDesc<T>[] cds = new BeanColumnDesc[tmpCds.length];

        int i = 0;
        HEADER: for (final String headerElem : header) {
            for (final BeanColumnDesc<T> cd : tmpCds) {
                final ColumnName name = cd.getName();
                if (name.getLabel().equals(headerElem)) {
                    final PropertyDesc<T> pd = getPropertyDesc(beanDesc, name
                        .getName());
                    cd.setPropertyDesc(pd);

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
        columnSetup.setup();
        final List<ColumnName> names = columnSetup.columnNames;
        final ColumnName[] a = names.toArray(new ColumnName[names.size()]);
        setColumns(a);
    }

    public static interface Block<T> {
        void run(T t);
    }

    public static abstract class ColumnSetup {

        public abstract void setup();

        private final List<ColumnName> columnNames = CollectionsUtil
            .newArrayList();

        protected final void column(final String name) {
            columnNames.add(new SimpleColumnName(name));
        }

        protected final void column(final String propertyName,
            final String label) {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            columnNames.add(n);
        }

    }

}
