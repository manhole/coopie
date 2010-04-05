package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public class BeanColumnLayout<T> extends AbstractColumnLayout<T> {

    private final BeanDesc<T> beanDesc;

    public BeanColumnLayout(final Class<T> beanClass) {
        this.beanDesc = BeanDescFactory.getBeanDesc(beanClass);
    }

    public BeanDesc<T> getBeanDesc() {
        return beanDesc;
    }

    @Override
    protected ColumnDesc<T>[] getColumnDescs() {
        if (columnDescs != null) {
            return columnDescs;
        }

        if (columnNames == null || columnNames.isEmpty()) {
            /*
             * beanの全プロパティを対象に。
             */
            final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
            final ColumnDesc<T>[] cds = newColumnDescs(pds.size());
            int i = 0;
            for (final PropertyDesc<T> pd : pds) {
                final String propertyName = pd.getPropertyName();
                final ColumnName columnName = new SimpleColumnName(propertyName);
                final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pd);
                cds[i] = cd;
                i++;
            }
            orderSpecified = OrderSpecified.NO;
            columnDescs = cds;
        } else {
            /*
             * 設定されているプロパティ名を対象に。
             */
            final ColumnName[] names = columnNames.getColumnNames();
            final ColumnDesc<T>[] cds = newColumnDescs(names.length);
            int i = 0;
            for (final ColumnName columnName : names) {
                final String propertyName = columnName.getName();
                final PropertyDesc<T> pd = getPropertyDesc(beanDesc,
                    propertyName);
                final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pd);
                cds[i] = cd;
                i++;
            }
            orderSpecified = OrderSpecified.SPECIFIED;
            columnDescs = cds;
        }
        return columnDescs;
    }

    private ColumnDesc<T> newBeanColumnDesc(final ColumnName name,
        final PropertyDesc<T> pd) {
        final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
        cd.setPropertyDesc(pd);
        cd.setName(name);
        return cd;
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

    static class BeanColumnDesc<T> implements ColumnDesc<T> {

        /**
         * CSV列名。
         */
        private ColumnName name;

        private PropertyDesc<T> propertyDesc;

        @Override
        public ColumnName getName() {
            return name;
        }

        public void setName(final ColumnName name) {
            this.name = name;
        }

        public PropertyDesc<T> getPropertyDesc() {
            return propertyDesc;
        }

        public void setPropertyDesc(final PropertyDesc<T> propertyDesc) {
            this.propertyDesc = propertyDesc;
        }

        @Override
        public String getValue(final T bean) {
            final Object v = propertyDesc.getValue(bean);
            // TODO null値の場合
            return String.valueOf(v);
        }

        @Override
        public void setValue(final T bean, final String value) {
            propertyDesc.setValue(bean, value);
        }

    }

}
