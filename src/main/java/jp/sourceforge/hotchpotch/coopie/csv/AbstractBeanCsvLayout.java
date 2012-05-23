package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.ClassDesc;
import org.t2framework.commons.meta.MethodDesc;
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

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            /*
             * アノテーションが付いている場合は、アノテーションを優先する
             */
            final RecordDesc<T> recordDesc = createByAnnotation();
            setRecordDesc(recordDesc);
        }

        if (getRecordDesc() == null) {
            /*
             * beanの全プロパティを対象に。
             */
            final RecordDesc<T> recordDesc = setupByProperties();
            setRecordDesc(recordDesc);
        }

        if (getRecordDesc() == null) {
            throw new AssertionError();
        }
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

            // TODO converter
            final PropertyBinding<T, Object> pb = new BeanPropertyBinding<T, Object>(
                    c.getPropertyDesc());
            final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pb,
                    PassthroughStringConverter.getInstance());
            cds[i] = cd;
        }
        // TODO アノテーションのorderが全て指定されていた場合はSPECIFIEDにするべきでは?
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
            // TODO converter
            final PropertyBinding<T, Object> pb = new BeanPropertyBinding<T, Object>(
                    pd);
            final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pb,
                    PassthroughStringConverter.getInstance());
            cds[i] = cd;
            i++;
        }

        return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                new BeanRecordType<T>(beanDesc_));
    }

    private static <U> ColumnDesc<U> newBeanColumnDesc(final ColumnName name,
            final PropertyBinding<U, Object> propertyBinding,
            final Converter converter) {
        final BeanColumnDesc<U> cd = new BeanColumnDesc<U>();
        cd.setPropertyBinding(propertyBinding);
        cd.setName(name);
        cd.setConverter(converter);
        return cd;
    }

    private static <U> ColumnDesc<U>[] newCompositBeanColumnDesc(
            final List<ColumnName> names,
            final List<PropertyBinding<U, Object>> propertyBindings,
            final Converter converter) {

        final CompositColumnDesc ccd = new CompositColumnDesc();
        ccd.setPropertyBindings(propertyBindings);
        ccd.setColumnNames(names);
        ccd.setConverter(converter);
        return ccd.getColumnDescs();
    }

    // TODO
    public static <U> ColumnDesc<U>[] toColumnDescs(
            final Collection<? extends InternalColumnBuilder> builders,
            final BeanDesc<U> bd) {

        final List<ColumnDesc<U>> list = CollectionsUtil.newArrayList();
        for (final InternalColumnBuilder builder : builders) {
            final List<ColumnName> columnNames = builder.getColumnNames();
            final List<String> propertyNames = builder.getPropertyNames();
            final List<PropertyBinding<U, Object>> pbs = CollectionsUtil
                    .newArrayList();
            for (final String propertyName : propertyNames) {
                final PropertyBinding<U, Object> pb = getPropertyBinding(bd,
                        propertyName);
                pbs.add(pb);
            }
            if (pbs.isEmpty()) {
                throw new IllegalStateException(
                        "property is not specified. for column {" + columnNames
                                + "}");
            }
            if (columnNames.size() == 1 && pbs.size() == 1) {
                final ColumnDesc<U> cd = newBeanColumnDesc(columnNames.get(0),
                        pbs.get(0), builder.getConverter());
                list.add(cd);
            } else {
                final ColumnDesc<U>[] cds = newCompositBeanColumnDesc(
                        columnNames, pbs, builder.getConverter());
                Collections.addAll(list, cds);
            }
        }
        final ColumnDesc<U>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private static <U> PropertyBinding<U, Object> getPropertyBinding(
            final BeanDesc<U> beanDesc, final String name) {

        final PropertyDesc<U> pd = beanDesc.getPropertyDesc(name);
        if (pd == null) {
            final ClassDesc<U> classDesc = beanDesc.getClassDesc();
            final Class<? extends U> concreteClass = classDesc
                    .getConcreteClass();
            final String className = concreteClass.getName();
            throw new PropertyNotFoundException("property not found:<" + name
                    + "> for class:<" + className + ">");
        }
        return new BeanPropertyBinding<U, Object>(pd);
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
        private RecordDesc<T> recordDesc_;

        BeanCsvRecordDescSetup(final BeanDesc<T> beanDesc) {
            beanDesc_ = beanDesc;
        }

        @Override
        public RecordDesc<T> getRecordDesc() {
            buildIfNeed();
            return recordDesc_;
        }

        private void buildIfNeed() {
            if (recordDesc_ != null) {
                return;
            }
            /*
             * 設定されているプロパティ名を対象に。
             */
            final ColumnDesc<T>[] cds = toColumnDescs(getColumnBuilders(),
                    beanDesc_);
            recordDesc_ = new DefaultRecordDesc<T>(cds,
                    OrderSpecified.SPECIFIED, new BeanRecordType<T>(beanDesc_));
        }
    }

    static class PropertyNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public PropertyNotFoundException(final String message) {
            super(message);
        }

    }

    public static class CompositColumnDesc<T> {

        private List<ColumnName> columnNames_;
        private List<PropertyBinding> propertyBindings_;

        @SuppressWarnings("rawtypes")
        private Converter converter_;
        private Map<ColumnName, String> getValues_;
        private Map<ColumnName, String> setValues_;

        public ColumnDesc<T>[] getColumnDescs() {
            final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(columnNames_
                    .size());
            int i = 0;
            for (final ColumnName columnName : columnNames_) {
                cds[i] = new Adapter(columnName);
                i++;
            }
            return cds;
        }

        public Converter getConverter() {
            return converter_;
        }

        public void setColumnNames(final List<ColumnName> columnNames) {
            columnNames_ = columnNames;
        }

        public void setPropertyBindings(
                final List<PropertyBinding> propertyBindings) {
            propertyBindings_ = propertyBindings;
        }

        public void setConverter(final Converter converter) {
            converter_ = converter;
        }

        private String getValue(final ColumnName columnName, final T bean) {
            if (getValues_ == null || !getValues_.containsKey(columnName)) {
                final Object[] from = new Object[propertyBindings_.size()];
                {
                    int i = 0;
                    for (final PropertyBinding<T, Object> binding : propertyBindings_) {
                        final Object v = binding.getValue(bean);
                        from[i] = v;
                        i++;
                    }
                }

                // TODO 引数が配列の場合
                // TODO 戻り値が配列ではない場合
                final Object[] to = (Object[]) converter_.convertTo(from[0]);
                getValues_ = CollectionsUtil.newHashMap();
                {
                    int i = 0;
                    for (final ColumnName name : columnNames_) {
                        final Object value = to[i];
                        final String s = StringUtil.toString(value);
                        getValues_.put(name, s);
                        i++;
                    }
                }
            }

            final String v = getValues_.remove(columnName);
            if (getValues_.isEmpty()) {
                getValues_ = null;
            }
            return v;
        }

        private void setValue(final ColumnName columnName, final T bean,
                final String value) {
            if (setValues_ == null || setValues_.containsKey(columnName)) {
                setValues_ = CollectionsUtil.newHashMap();
            }
            setValues_.put(columnName, value);

            if (setValues_.size() == columnNames_.size()) {
                // TODO 引数が配列ではない場合
                Class<?> componentType;
                {
                    final MethodDesc methodDesc = BeanDescFactory.getBeanDesc(
                            converter_.getClass()).getMethodDesc("convertFrom");
                    final Class<?>[] parameterTypes = methodDesc
                            .getParameterTypes();
                    componentType = parameterTypes[0].getComponentType();
                }
                final Object[] from = (Object[]) Array.newInstance(
                        componentType, columnNames_.size());
                {
                    int i = 0;
                    for (final ColumnName name : columnNames_) {
                        final String s = setValues_.get(name);
                        from[i] = s;
                        i++;
                    }
                }

                final Object to = converter_.convertFrom(from);
                setValues_ = null;
                // TODO プロパティ側が複数の場合
                propertyBindings_.get(0).setValue(bean, to);
                // TODO
                //                for (final PropertyBinding<T, Object> binding : propertyBindings_) {
                //                    final Object v = to.get();
                //                    binding.setValue(bean, v);
                //                }
            }
        }

        class Adapter implements ColumnDesc<T> {

            private final ColumnName columnName_;

            Adapter(final ColumnName columnName) {
                columnName_ = columnName;
            }

            @Override
            public ColumnName getName() {
                return columnName_;
            }

            @Override
            public String getValue(final T bean) {
                final String v = CompositColumnDesc.this.getValue(columnName_,
                        bean);
                return v;
            }

            @Override
            public void setValue(final T bean, final String value) {
                CompositColumnDesc.this.setValue(columnName_, bean, value);
            }

        }

    }

    static class BeanColumnDesc<T> implements ColumnDesc<T> {

        /**
         * CSV列名。
         */
        private ColumnName columnName_;
        private PropertyBinding<T, Object> propertyBinding_;
        private Converter converter_;

        @Override
        public ColumnName getName() {
            return columnName_;
        }

        public void setName(final ColumnName name) {
            columnName_ = name;
        }

        public PropertyBinding<T, Object> getPropertyBinding() {
            return propertyBinding_;
        }

        public void setPropertyBinding(
                final PropertyBinding<T, Object> propertyBinding) {
            propertyBinding_ = propertyBinding;
        }

        public Converter getConverter() {
            return converter_;
        }

        public void setConverter(final Converter converter) {
            converter_ = converter;
        }

        @Override
        public String getValue(final T bean) {
            final Object from = propertyBinding_.getValue(bean);
            final Object to = converter_.convertTo(from);
            return StringUtil.toString(to);
        }

        @Override
        public void setValue(final T bean, final String value) {
            final String from = value;
            final Object to = converter_.convertFrom(from);
            propertyBinding_.setValue(bean, to);
        }
    }

}
