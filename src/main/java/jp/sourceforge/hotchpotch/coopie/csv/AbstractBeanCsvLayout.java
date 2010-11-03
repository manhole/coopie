package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public abstract class AbstractBeanCsvLayout<T> extends AbstractCsvLayout<T> {

    private final BeanDesc<T> beanDesc;

    public AbstractBeanCsvLayout(final Class<T> beanClass) {
        beanDesc = BeanDescFactory.getBeanDesc(beanClass);
    }

    protected RecordDesc<T> buildRecordDesc() {
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

            return new DefaultRecordDesc<T>(cds, OrderSpecified.NO, withHeader,
                    new BeanRecordType<T>(beanDesc));
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

            return new DefaultRecordDesc<T>(cds, OrderSpecified.SPECIFIED,
                    withHeader, new BeanRecordType<T>(beanDesc));
        }
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
            throw new IllegalStateException("property not found:<" + name + ">");
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
            if (v == null) {
                return null;
            }
            return String.valueOf(v);
        }

        @Override
        public void setValue(final T bean, final String value) {
            propertyDesc.setValue(bean, value);
        }

    }

    static class BeanRecordType<T> implements RecordType<T> {

        private final BeanDesc<T> beanDesc;

        public BeanRecordType(final BeanDesc<T> beanDesc) {
            this.beanDesc = beanDesc;
        }

        @Override
        public T newInstance() {
            return beanDesc.newInstance();
        }

    }

}