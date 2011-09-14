package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.AbstractCsvRecordDescSetup.SimpleColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public abstract class AbstractBeanCsvLayout<T> extends AbstractCsvLayout<T> {

    private final BeanDesc<T> beanDesc;

    public AbstractBeanCsvLayout(final Class<T> beanClass) {
        beanDesc = BeanDescFactory.getBeanDesc(beanClass);
    }

    @Override
    protected CsvRecordDescSetup<T> getRecordDescSetup() {
        return new BeanCsvRecordDescSetup<T>(beanDesc);
    }

    protected RecordDesc<T> getRecordDesc() {
        if (recordDesc == null) {
            /*
             * beanの全プロパティを対象に。
             */
            final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
            final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(pds.size());
            int i = 0;
            for (final PropertyDesc<T> pd : pds) {
                final String propertyName = pd.getPropertyName();
                final ColumnName columnName = new SimpleColumnName(propertyName);
                final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pd,
                        (Converter) null);
                cds[i] = cd;
                i++;
            }

            return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                    new BeanRecordType<T>(beanDesc));
        }
        return recordDesc;
    }

    static class BeanCsvRecordDescSetup<T> extends
            AbstractCsvRecordDescSetup<T> {

        private final BeanDesc<T> beanDesc;

        BeanCsvRecordDescSetup(final BeanDesc<T> beanDesc) {
            this.beanDesc = beanDesc;
        }

        @Override
        public RecordDesc<T> getRecordDesc() {
            /*
             * 設定されているプロパティ名を対象に。
             */
            final ColumnDesc<T>[] cds = toColumnDescs(columnBuilders, beanDesc);

            return new DefaultRecordDesc<T>(cds, OrderSpecified.SPECIFIED,
                    new BeanRecordType<T>(beanDesc));
        }

    }

    // TODO
    static <U> ColumnDesc<U>[] toColumnDescs(
            final List<SimpleColumnBuilder> columns, final BeanDesc<U> bd) {
        final ColumnDesc<U>[] cds = ColumnDescs.newColumnDescs(columns.size());
        int i = 0;
        for (final SimpleColumnBuilder builder : columns) {
            final ColumnName columnName = builder.getColumnName();
            final String propertyName = columnName.getName();
            final PropertyDesc<U> pd = getPropertyDesc(bd, propertyName);
            final ColumnDesc<U> cd = newBeanColumnDesc(columnName, pd,
                    builder.getConverter());
            cds[i] = cd;
            i++;
        }
        return cds;
    }

    private static <U> ColumnDesc<U> newBeanColumnDesc(final ColumnName name,
            final PropertyDesc<U> pd, final Converter converter) {
        final BeanColumnDesc<U> cd = new BeanColumnDesc<U>();
        cd.setPropertyDesc(pd);
        cd.setName(name);
        cd.setConverter(converter);
        return cd;
    }

    private static <U> PropertyDesc<U> getPropertyDesc(
            final BeanDesc<U> beanDesc, final String name) {
        final PropertyDesc<U> pd = beanDesc.getPropertyDesc(name);
        if (pd == null) {
            throw new IllegalStateException("property not found:<" + name + ">");
        }
        return pd;
    }

    static class BeanColumnDesc<T> implements ColumnDesc<T> {

        /**
         * CSV列名。
         */
        private ColumnName name_;

        private PropertyDesc<T> propertyDesc_;

        @SuppressWarnings("rawtypes")
        private Converter converter_;

        @Override
        public ColumnName getName() {
            return name_;
        }

        public void setName(final ColumnName name) {
            this.name_ = name;
        }

        public PropertyDesc<T> getPropertyDesc() {
            return propertyDesc_;
        }

        public void setPropertyDesc(final PropertyDesc<T> propertyDesc) {
            this.propertyDesc_ = propertyDesc;
        }

        public Converter getConverter() {
            return converter_;
        }

        public void setConverter(final Converter converter) {
            if (converter != null) {
                converter_ = converter;
                return;
            }
            converter_ = NullConverter.getInstance();
        }

        @SuppressWarnings("unchecked")
        @Override
        public String getValue(final T bean) {
            final Object v = propertyDesc_.getValue(bean);
            if (v == null) {
                return null;
            }

            final PassthroughObjectRepresentation from = new PassthroughObjectRepresentation(
                    v);
            final StringExternalRepresentation to = new StringExternalRepresentation();
            converter_.convertTo(from, to);
            final String s = to.get();
            return s;
        }

        @Override
        public void setValue(final T bean, final String value) {
            final StringExternalRepresentation from = new StringExternalRepresentation(
                    value);
            final PassthroughObjectRepresentation to = new PassthroughObjectRepresentation();
            converter_.convertFrom(from, to);
            final Object obj = to.get();
            propertyDesc_.setValue(bean, obj);
        }

    }

    private static class StringExternalRepresentation implements
            Converter.ExternalRepresentation<String> {

        private final Deque<String> list_ = new LinkedList<String>();

        StringExternalRepresentation() {
        }

        StringExternalRepresentation(final String s) {
            add(s);
        }

        @Override
        public void add(final String s) {
            list_.add(s);
        }

        @Override
        public String get() {
            return list_.remove();
        }

    }

    private static class PassthroughObjectRepresentation implements
            Converter.ObjectRepresentation<Object> {

        private final Deque<Object> list_ = new LinkedList<Object>();

        PassthroughObjectRepresentation() {
        }

        PassthroughObjectRepresentation(final Object o) {
            add(o);
        }

        @Override
        public void add(final Object o) {
            list_.add(o);
        }

        @Override
        public Object get() {
            return list_.remove();
        }

    }

}
