package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

public abstract class AbstractBeanCsvLayout<T> extends AbstractCsvLayout<T> {

    private final BeanDesc<T> beanDesc_;

    public AbstractBeanCsvLayout(final Class<T> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    @Override
    protected CsvRecordDescSetup<T> getRecordDescSetup() {
        return new BeanCsvRecordDescSetup<T>(beanDesc_);
    }

    @Override
    protected RecordDesc<T> getRecordDesc() {
        if (super.getRecordDesc() == null) {
            /*
             * アノテーションが付いている場合は、アノテーションを優先する
             */
            final RecordDesc<T> recordDesc = createByAnnotation();
            super.setRecordDesc(recordDesc);
        }

        if (super.getRecordDesc() == null) {
            /*
             * beanの全プロパティを対象に。
             */
            final RecordDesc<T> recordDesc = setupByProperties();
            super.setRecordDesc(recordDesc);
        }

        if (super.getRecordDesc() == null) {
            throw new AssertionError();
        }
        return super.getRecordDesc();
    }

    private RecordDesc<T> createByAnnotation() {
        final List<CsvColumnValue<T>> list = CollectionsUtil.newArrayList();
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final CsvColumn column = Annotations.getAnnotation(pd,
                    CsvColumn.class);
            if (column == null) {
                continue;
            }
            final CsvColumnValue<T> c = new CsvColumnValue<T>(pd, column);
            list.add(c);
        }

        if (list.isEmpty()) {
            return null;
        }

        Collections.sort(list);

        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        for (int i = 0; i < list.size(); i++) {
            final CsvColumnValue<T> c = list.get(i);
            final ColumnName columnName = c.getColumnName();

            final ColumnDesc<T> cd = newBeanColumnDesc(columnName,
                    c.getPropertyDesc());
            cds[i] = cd;
        }
        return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                new BeanRecordType<T>(beanDesc_));
    }

    private RecordDesc<T> setupByProperties() {
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
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
                new BeanRecordType<T>(beanDesc_));
    }

    private static class CsvColumnValue<T> implements
            Comparable<CsvColumnValue<T>> {

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

        @Override
        public int compareTo(final CsvColumnValue<T> o) {
            // orderが小さい方を左側に
            final int ret = getOrder() - o.getOrder();
            return ret;
        }

    }

    static class BeanCsvRecordDescSetup<T> extends
            AbstractCsvRecordDescSetup<T> {

        private final BeanDesc<T> beanDesc_;

        BeanCsvRecordDescSetup(final BeanDesc<T> beanDesc) {
            beanDesc_ = beanDesc;
        }

        @Override
        public RecordDesc<T> getRecordDesc() {
            /*
             * 設定されているプロパティ名を対象に。
             */
            final ColumnDesc<T>[] cds = toColumnDescs(columnNames_, beanDesc_);

            return new DefaultRecordDesc<T>(cds, OrderSpecified.SPECIFIED,
                    new BeanRecordType<T>(beanDesc_));
        }

    }

    // TODO
    public static <U> ColumnDesc<U>[] toColumnDescs(
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
        private ColumnName name_;

        private PropertyDesc<T> propertyDesc_;

        @Override
        public ColumnName getName() {
            return name_;
        }

        public void setName(final ColumnName name) {
            name_ = name;
        }

        public PropertyDesc<T> getPropertyDesc() {
            return propertyDesc_;
        }

        public void setPropertyDesc(final PropertyDesc<T> propertyDesc) {
            propertyDesc_ = propertyDesc;
        }

        @Override
        public String getValue(final T bean) {
            final Object v = propertyDesc_.getValue(bean);
            if (v == null) {
                return null;
            }
            return String.valueOf(v);
        }

        @Override
        public void setValue(final T bean, final String value) {
            propertyDesc_.setValue(bean, value);
        }

    }

}
