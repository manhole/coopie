package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;

public class BeanColumnLayout<T> {

    private BeanColumnDesc<T>[] columnDescs;
    // 一時的
    private List<ColumnName> columnNames;

    private BeanColumnDesc<T>[] getColumnDescs() {
        if (columnDescs == null) {
            if (columnNames == null) {
                return null;
            }
            columnDescs = new BeanColumnDesc[columnNames.size()];
            int i = 0;
            for (final ColumnName columnName : columnNames) {
                final BeanColumnDesc<T> cd = new BeanColumnDesc();
                cd.setName(columnName);
                columnDescs[i] = cd;
                i++;
            }
        }
        return columnDescs;
    }

    public void setup(final BeanDesc<T> beanDesc) {
        final BeanColumnDesc<T>[] cds = getColumnDescs();
        if (cds == null) {
            final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
            columnDescs = new BeanColumnDesc[pds.size()];
            int i = 0;
            for (final PropertyDesc<T> pd : pds) {
                final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                cd.setPropertyDesc(pd);
                cd.setName(new SimpleColumnName(pd.getPropertyName()));
                columnDescs[i] = cd;
                i++;
            }
        } else {
            for (int i = 0; i < cds.length; i++) {
                final BeanColumnDesc<T> cd = cds[i];
                final String name = cd.getName().getName();
                final PropertyDesc<T> pd = getPropertyDesc(beanDesc, name);
                cd.setPropertyDesc(pd);
            }
            columnDescs = cds;
        }
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

    public void setNames(final String[] names) {
        for (int i = 0; i < names.length; i++) {
            final String name = names[i];
            final ColumnName columnName = new SimpleColumnName(name);
            addColumnName(columnName);
        }
    }

    public void addAlias(final String alias, final String propertyName) {
        final SimpleColumnName columnName = new SimpleColumnName();
        columnName.setLabel(alias);
        columnName.setName(propertyName);
        addColumnName(columnName);
    }

    private void addColumnName(final ColumnName columnName) {
        if (columnNames == null) {
            columnNames = CollectionsUtil.newArrayList();
        }
        columnNames.add(columnName);
    }

    public String[] getValues(final T bean) {
        final BeanColumnDesc<T>[] cds = getColumnDescs();
        final String[] line = new String[cds.length];
        int i = 0;
        for (int j = 0; j < cds.length; j++) {
            final BeanColumnDesc<T> cd = cds[j];
            final String v = cd.getValue(bean);
            line[i] = v;
            i++;
        }
        return line;
    }

    public void setValues(final T bean, final String[] line) {
        for (int i = 0; i < line.length; i++) {
            final String elem = line[i];
            final BeanColumnDesc<T> cd = columnDescs[i];
            cd.setValue(bean, elem);
        }
    }

    public void setupColumnDescByHeader(final BeanDesc<T> beanDesc,
        final String[] header) {

        if (getColumnDescs() == null) {
            /*
             * CSVヘッダ名をbeanのプロパティ名として扱う。
             */
            final BeanColumnDesc<T>[] cds = new BeanColumnDesc[header.length];
            for (int i = 0; i < header.length; i++) {
                final String headerElem = header[i];
                final PropertyDesc<T> pd = getPropertyDesc(beanDesc, headerElem);
                final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                final ColumnName columnName = new SimpleColumnName(pd
                    .getPropertyName());
                cd.setName(columnName);
                cd.setPropertyDesc(pd);
                cds[i] = cd;
            }
            columnDescs = cds;
        } else {
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
                        final PropertyDesc<T> pd = getPropertyDesc(beanDesc,
                            name.getName());
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

}
