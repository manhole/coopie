package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.MethodDesc;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

public abstract class AbstractBeanCsvLayout<T> extends AbstractCsvLayout<T> {

    private final BeanDesc<T> beanDesc_;
    private RecordDefCustomizer customizer_ = EmptyRecordDefCustomizer
            .getInstance();

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
        final BeanRecordDef<T> recordDef = new BeanRecordDef<T>();
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final CsvColumns columns = Annotations.getAnnotation(pd,
                    CsvColumns.class);
            if (columns != null) {
                final BeanCsvColumnsDef<T> columnsDef = new BeanCsvColumnsDef<T>();
                columnsDef.setup(columns, pd);
                recordDef.addColumnsDef(columnsDef);
                continue;
            }
            // TODO: CsvColumnとCsvColumnsの両方があったら例外にすること

            final CsvColumn column = Annotations.getAnnotation(pd,
                    CsvColumn.class);
            if (column != null) {
                final BeanCsvColumnDef<T> columnDef = new BeanCsvColumnDef<T>();
                columnDef.setup(column, pd);
                recordDef.addColumnDef(columnDef);
            }
        }

        if (recordDef.getColumnDefs().isEmpty()
                && recordDef.getColumnsDefs().isEmpty()) {
            return null;
        }

        Collections.sort(recordDef.getColumnDefs(),
                CsvColumnDefComparator.getInstance());
        customizer_.customize(recordDef);

        final List<ColumnDesc<T>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list);
        appendColumnDescFromColumnsDef(recordDef, list);
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);

        // TODO アノテーションのorderが全て指定されていた場合はSPECIFIEDにするべきでは?
        return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                new BeanRecordType<T>(beanDesc_));
    }

    private RecordDesc<T> setupByProperties() {
        final BeanRecordDef<T> recordDef = new BeanRecordDef<T>();
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final String propertyName = pd.getPropertyName();
            final BeanCsvColumnDef<T> columnDef = new BeanCsvColumnDef<T>();
            columnDef.setLabel(propertyName);
            //orderは未指定とする
            //c.setOrder();
            columnDef.setPropertyDesc(pd);
            recordDef.addColumnDef(columnDef);
        }

        customizer_.customize(recordDef);

        final List<ColumnDesc<T>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list);
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);

        return new DefaultRecordDesc<T>(cds, OrderSpecified.NO,
                new BeanRecordType<T>(beanDesc_));
    }

    private void appendColumnDescFromColumnDef(final RecordDef recordDef,
            final List<ColumnDesc<T>> list) {
        for (final CsvColumnDef columnDef : recordDef.getColumnDefs()) {
            final ColumnName columnName = columnDef.getColumnName();
            final PropertyDesc<T> pd = beanDesc_.getPropertyDesc(columnDef
                    .getPropertyName());
            final PropertyBinding<T, Object> pb = new BeanPropertyBinding<T, Object>(
                    pd);
            final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pb,
                    columnDef.getConverter());
            list.add(cd);
        }
    }

    private void appendColumnDescFromColumnsDef(final RecordDef recordDef,
            final List<ColumnDesc<T>> list) {
        for (final CsvColumnsDef columnsDef : recordDef.getColumnsDefs()) {
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            for (final CsvColumnDef columnDef : columnsDef.getColumnDefs()) {
                columnNames.add(columnDef.getColumnName());
            }
            final PropertyDesc<T> pd = beanDesc_.getPropertyDesc(columnsDef
                    .getPropertyName());
            final PropertyBinding<T, Object> pb = new BeanPropertyBinding<T, Object>(
                    pd);
            final ColumnDesc<T>[] cds = newCompositBeanColumnDesc(columnNames,
                    pb, columnsDef.getConverter());
            Collections.addAll(list, cds);
        }
    }

    public void setCustomizer(final RecordDefCustomizer columnCustomizer) {
        customizer_ = columnCustomizer;
    }

    static <U> ColumnDesc<U> newBeanColumnDesc(final ColumnName columnName,
            final PropertyBinding propertyBinding, final Converter converter) {
        final DefaultColumnDesc<U> cd = new DefaultColumnDesc<U>();
        cd.setName(columnName);
        cd.setPropertyBinding(propertyBinding);
        cd.setConverter(converter);
        return cd;
    }

    private static <U> ColumnDesc<U>[] newCompositBeanColumnDesc(
            final List<ColumnName> names,
            final PropertyBinding<U, Object> propertyBinding,
            final Converter converter) {

        final CompositColumnDesc ccd = new CompositColumnDesc();
        ccd.setPropertyBinding(propertyBinding);
        ccd.setColumnNames(names);
        ccd.setConverter(converter);
        return ccd.getColumnDescs();
    }

    // TODO
    public static <U> ColumnDesc<U>[] toColumnDescs(
            final Collection<? extends InternalColumnBuilder> builders,
            final PropertyBindingFactory<U> pbf) {

        final List<ColumnDesc<U>> list = CollectionsUtil.newArrayList();
        for (final InternalColumnBuilder builder : builders) {
            final List<ColumnName> columnNames = builder.getColumnNames();
            final String propertyName = builder.getPropertyName();
            final PropertyBinding<U, Object> pb;
            if (!StringUtil.isEmpty(propertyName)) {
                pb = pbf.getPropertyBinding(propertyName);
            } else {
                // プロパティ名がカラム名と同じとみなす
                if (columnNames.size() == 1) {
                    pb = pbf.getPropertyBinding(columnNames.get(0).getLabel());
                } else {
                    throw new IllegalStateException(
                            "property is not specified. for column {"
                                    + columnNames + "}");
                }
            }
            if (columnNames.size() == 1) {
                final ColumnDesc<U> cd = newBeanColumnDesc(columnNames.get(0),
                        pb, builder.getConverter());
                list.add(cd);
            } else {
                final ColumnDesc<U>[] cds = newCompositBeanColumnDesc(
                        columnNames, pb, builder.getConverter());
                Collections.addAll(list, cds);
            }
        }
        final ColumnDesc<U>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private static class BeanCsvColumnDef<BEAN> implements CsvColumnDef,
            Comparable<BeanCsvColumnDef<BEAN>> {

        private PropertyDesc<BEAN> propertyDesc_;

        private String label_;
        private int order_;
        private Converter<?, ?> converter_ = PassthroughStringConverter
                .getInstance();

        private ColumnName columnName_;

        public void setup(final CsvColumn column, final PropertyDesc<BEAN> pd) {
            if (StringUtil.isBlank(column.label())) {
                setLabel(pd.getPropertyName());
            } else {
                setLabel(column.label());
            }
            setOrder(column.order());
            setPropertyDesc(pd);
        }

        @Override
        public String getLabel() {
            return label_;
        }

        @Override
        public void setLabel(final String label) {
            label_ = label;
        }

        @Override
        public int getOrder() {
            return order_;
        }

        @Override
        public void setOrder(final int order) {
            order_ = order;
        }

        @Override
        public Converter<?, ?> getConverter() {
            return converter_;
        }

        @Override
        public void setConverter(final Converter<?, ?> converter) {
            converter_ = converter;
        }

        @Override
        public String getPropertyName() {
            return propertyDesc_.getPropertyName();
        }

        @Override
        public Class<?> getPropertyType() {
            return propertyDesc_.getPropertyType();
        }

        public PropertyDesc<BEAN> getPropertyDesc() {
            return propertyDesc_;
        }

        public void setPropertyDesc(final PropertyDesc<BEAN> propertyDesc) {
            propertyDesc_ = propertyDesc;
        }

        @Override
        public int compareTo(final BeanCsvColumnDef<BEAN> o) {
            // orderが小さい方を左側に
            final int ret = getOrder() - o.getOrder();
            return ret;
        }

        @Override
        public ColumnName getColumnName() {
            if (columnName_ == null) {
                final SimpleColumnName columnName = new SimpleColumnName();
                columnName.setName(getPropertyDesc().getPropertyName());
                columnName.setLabel(getLabel());
                columnName_ = columnName;
            }
            return columnName_;
        }

        public void setColumnName(final ColumnName columnName) {
            columnName_ = columnName;
        }

    }

    private static class BeanCsvColumnsDef<BEAN> implements CsvColumnsDef {

        private PropertyDesc<BEAN> propertyDesc_;
        private Converter<?, ?> converter_ = PassthroughStringConverter
                .getInstance();
        private final List<CsvColumnDef> columnDefs_ = CollectionsUtil
                .newArrayList();

        public void setup(final CsvColumns columns, final PropertyDesc<BEAN> pd) {
            for (final CsvColumn column : columns.value()) {
                final BeanCsvColumnDef<BEAN> columnDef = new BeanCsvColumnDef<BEAN>();
                if (StringUtil.isBlank(column.label())) {
                    columnDef.setLabel(pd.getPropertyName());
                } else {
                    columnDef.setLabel(column.label());
                }
                columnDef.setOrder(column.order());
                columnDef.setColumnName(new SimpleColumnName(columnDef
                        .getLabel()));
                addColumnDef(columnDef);
            }
            setPropertyDesc(pd);
        }

        @Override
        public List<CsvColumnDef> getColumnDefs() {
            return columnDefs_;
        }

        public void addColumnDef(final CsvColumnDef columnDef) {
            columnDefs_.add(columnDef);
        }

        @Override
        public Converter<?, ?> getConverter() {
            return converter_;
        }

        @Override
        public void setConverter(final Converter<?, ?> converter) {
            converter_ = converter;
        }

        @Override
        public String getPropertyName() {
            return propertyDesc_.getPropertyName();
        }

        @Override
        public Class<?> getPropertyType() {
            return propertyDesc_.getPropertyType();
        }

        public PropertyDesc<BEAN> getPropertyDesc() {
            return propertyDesc_;
        }

        public void setPropertyDesc(final PropertyDesc<BEAN> propertyDesc) {
            propertyDesc_ = propertyDesc;
        }

    }

    static class BeanRecordDef<BEAN> implements RecordDef {

        final List<CsvColumnDef> columnDefs_ = CollectionsUtil.newArrayList();
        final List<CsvColumnsDef> columnsDefs_ = CollectionsUtil.newArrayList();

        @Override
        public void addColumnDef(final CsvColumnDef columnDef) {
            columnDefs_.add(columnDef);
        }

        @Override
        public List<? extends CsvColumnDef> getColumnDefs() {
            return columnDefs_;
        }

        @Override
        public void addColumnsDef(final CsvColumnsDef columnsDef) {
            columnsDefs_.add(columnsDef);
        }

        @Override
        public List<? extends CsvColumnsDef> getColumnsDefs() {
            return columnsDefs_;
        }

    }

    static class CsvColumnDefComparator implements Comparator<CsvColumnDef> {

        private static CsvColumnDefComparator INSTANCE = new CsvColumnDefComparator();

        public static CsvColumnDefComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int compare(final CsvColumnDef o1, final CsvColumnDef o2) {
            // orderが小さい方を左側に
            final int ret = o1.getOrder() - o2.getOrder();
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
            final PropertyBindingFactory<T> pbf = new BeanPropertyBinding.Factory<T>(
                    beanDesc_);
            final ColumnDesc<T>[] cds = toColumnDescs(getColumnBuilders(), pbf);
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
        private PropertyBinding propertyBinding_;

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

        public void setPropertyBinding(final PropertyBinding propertyBinding) {
            propertyBinding_ = propertyBinding;
        }

        public void setConverter(final Converter converter) {
            converter_ = converter;
        }

        private String getValue(final ColumnName columnName, final T bean) {
            if (getValues_ == null || !getValues_.containsKey(columnName)) {
                final Object from = propertyBinding_.getValue(bean);

                // TODO 戻り値が配列ではない場合
                final Object[] to = (Object[]) converter_.convertTo(from);
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
                propertyBinding_.setValue(bean, to);
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

    static class DefaultColumnDesc<T> implements ColumnDesc<T> {

        /**
         * CSV列名。
         */
        private ColumnName columnName_;
        private PropertyBinding propertyBinding_;
        private Converter converter_;

        @Override
        public ColumnName getName() {
            return columnName_;
        }

        public void setName(final ColumnName name) {
            columnName_ = name;
        }

        public PropertyBinding getPropertyBinding() {
            return propertyBinding_;
        }

        public void setPropertyBinding(final PropertyBinding propertyBinding) {
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
