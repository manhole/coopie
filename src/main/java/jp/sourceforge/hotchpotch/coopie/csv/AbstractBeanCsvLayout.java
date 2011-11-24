package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

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
             * アノテーションが付いている場合は、アノテーションを優先する
             */
            recordDesc = setupByAnnotation();
        }

        if (recordDesc == null) {
            /*
             * beanの全プロパティを対象に。
             */
            recordDesc = setupByProperties();
        }

        if (recordDesc == null) {
            throw new AssertionError();
        }
        return recordDesc;
    }

    private RecordDesc<T> setupByAnnotation() {
        final List<CsvColumnValue<T>> list = CollectionsUtil.newArrayList();
        final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final CsvColumn column = getCsvColumnAnnotation(pd);
            if (column == null) {
                continue;
            }
            final CsvColumnValue<T> c = new CsvColumnValue<T>(pd, column);
            list.add(c);
        }

        if (list.isEmpty()) {
            return null;
        }

        Collections.sort(list, new Comparator<CsvColumnValue<T>>() {
            @Override
            public int compare(final CsvColumnValue<T> o1,
                    final CsvColumnValue<T> o2) {
                // orderが小さい方を左側に
                final int ret = o1.getOrder() - o2.getOrder();
                return ret;
            }
        });

        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        for (int i = 0; i < list.size(); i++) {
            final CsvColumnValue<T> c = list.get(i);
            final ColumnName columnName = c.getColumnName();

            final ColumnDesc<T> cd = newBeanColumnDesc(columnName,
                    c.getPropertyDesc());
            cds[i] = cd;
        }
        return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                new BeanRecordType<T>(beanDesc));
    }

    private RecordDesc<T> setupByProperties() {
        final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(pds.size());
        int i = 0;
        for (final PropertyDesc<T> pd : pds) {
            final String propertyName = pd.getPropertyName();
            final ColumnName columnName = new SimpleColumnName(propertyName);
            final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pd);
            cds[i] = cd;
            i++;
        }

        return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                new BeanRecordType<T>(beanDesc));
    }

    private static class CsvColumnValue<T> {

        private final PropertyDesc<T> propertyDesc_;
        private final CsvColumn column_;

        public CsvColumnValue(final PropertyDesc<T> propertyDesc,
                final CsvColumn column) {
            propertyDesc_ = propertyDesc;
            column_ = column;
        }

        public ColumnName getColumnName() {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(getPropertyName());
            n.setLabel(getLabel());
            return n;
        }

        public int getOrder() {
            return column_.order();
        }

        private String getLabel() {
            final String s = column_.label();
            if (StringUtil.isBlank(s)) {
                return propertyDesc_.getPropertyName();
            }
            return s;
        }

        private String getPropertyName() {
            return propertyDesc_.getPropertyName();
        }

        public PropertyDesc<T> getPropertyDesc() {
            return propertyDesc_;
        }

    }

    private CsvColumn getCsvColumnAnnotation(final PropertyDesc<?> propertyDesc) {
        if (propertyDesc.isReadable()) {
            final Method reader = propertyDesc.getReadMethod();
            final CsvColumn annotation = reader.getAnnotation(CsvColumn.class);
            if (annotation != null) {
                return annotation;
            }
        }
        if (propertyDesc.isWritable()) {
            final Method writer = propertyDesc.getWriteMethod();
            final CsvColumn annotation = writer.getAnnotation(CsvColumn.class);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
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
            final ColumnDesc<T>[] cds = toColumnDescs(columnNames, beanDesc);

            return new DefaultRecordDesc<T>(cds, OrderSpecified.SPECIFIED,
                    new BeanRecordType<T>(beanDesc));
        }

    }

    // TODO
    static <U> ColumnDesc<U>[] toColumnDescs(
            final Collection<? extends ColumnName> columns, final BeanDesc<U> bd) {
        final ColumnDesc<U>[] cds = ColumnDescs.newColumnDescs(columns.size());
        int i = 0;
        for (final ColumnName columnName : columns) {
            final String propertyName = columnName.getName();
            final PropertyDesc<U> pd = getPropertyDesc(bd, propertyName);
            final ColumnDesc<U> cd = newBeanColumnDesc(columnName, pd);
            cds[i] = cd;
            i++;
        }
        return cds;
    }

    private static <U> ColumnDesc<U> newBeanColumnDesc(final ColumnName name,
            final PropertyDesc<U> pd) {
        final BeanColumnDesc<U> cd = new BeanColumnDesc<U>();
        cd.setPropertyDesc(pd);
        cd.setName(name);
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

}
