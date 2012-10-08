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
    private RecordDef recordDef_;

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
            final RecordDef recordDef = recordDef();
            customizer_.customize(recordDef);
            final ColumnDesc<T>[] cds = recordDefToColumnDesc(recordDef);
            // TODO アノテーションのorderが全て指定されていた場合はSPECIFIEDにするべきでは?
            final RecordDesc<T> recordDesc = new DefaultRecordDesc<T>(cds,
                    OrderSpecified.NO, new BeanRecordType<T>(beanDesc_));
            setRecordDesc(recordDesc);
        }

        if (getRecordDesc() == null) {
            throw new AssertionError();
        }
    }

    public RecordDef recordDef() {
        if (recordDef_ == null) {
            recordDef_ = createRecordDef();
        }
        return recordDef_;
    }

    private RecordDef createRecordDef() throws AssertionError {
        /*
         * アノテーションが付いている場合は、アノテーションを優先する
         */
        RecordDef recordDef = createRecordDefByAnnotation();
        if (recordDef == null) {
            /*
             * beanの全プロパティを対象に。
             */
            recordDef = createRecordDefByProperties();
        }
        if (recordDef == null) {
            throw new AssertionError();
        }
        return recordDef;
    }

    private ColumnDesc<T>[] recordDefToColumnDesc(final RecordDef recordDef) {
        final BeanPropertyBinding.Factory<T> pbf = new BeanPropertyBinding.Factory<T>(
                beanDesc_);
        final List<ColumnDesc<T>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list, pbf);
        appendColumnDescFromColumnsDef(recordDef, list, pbf);
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private DefaultRecordDef createRecordDefByAnnotation() {
        final DefaultRecordDef recordDef = new DefaultRecordDef();
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final CsvColumns columns = Annotations.getAnnotation(pd,
                    CsvColumns.class);
            if (columns != null) {
                final DefaultCsvColumnsDef columnsDef = new DefaultCsvColumnsDef();
                columnsDef.setup(columns, pd);
                recordDef.addColumnsDef(columnsDef);
                continue;
            }
            // TODO: CsvColumnとCsvColumnsの両方があったら例外にすること

            final CsvColumn column = Annotations.getAnnotation(pd,
                    CsvColumn.class);
            if (column != null) {
                final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
                columnDef.setup(column, pd);
                recordDef.addColumnDef(columnDef);
            }
        }

        if (recordDef.isEmpty()) {
            return null;
        }

        Collections.sort(recordDef.getColumnDefs(),
                CsvColumnDefComparator.getInstance());
        return recordDef;
    }

    private DefaultRecordDef createRecordDefByProperties() {
        final DefaultRecordDef recordDef = new DefaultRecordDef();
        final List<PropertyDesc<T>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<T> pd : pds) {
            final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
            // orderは未指定とする
            columnDef.setup(pd);
            recordDef.addColumnDef(columnDef);
        }
        return recordDef;
    }

    private static <T> void appendColumnDescFromColumnDef(
            final RecordDef recordDef, final List<ColumnDesc<T>> list,
            final PropertyBindingFactory<T> pbf) {
        for (final CsvColumnDef columnDef : recordDef.getColumnDefs()) {
            final ColumnName columnName = columnDef.getColumnName();
            final PropertyBinding<T, Object> pb = pbf
                    .getPropertyBinding(columnDef.getPropertyName());
            final ColumnDesc<T> cd = newBeanColumnDesc(columnName, pb,
                    columnDef.getConverter());
            list.add(cd);
        }
    }

    private static <T> void appendColumnDescFromColumnsDef(
            final RecordDef recordDef, final List<ColumnDesc<T>> list,
            final PropertyBindingFactory<T> pbf) {
        for (final CsvColumnsDef columnsDef : recordDef.getColumnsDefs()) {
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            for (final CsvColumnDef columnDef : columnsDef.getColumnDefs()) {
                columnNames.add(columnDef.getColumnName());
            }
            final PropertyBinding<T, Object> pb = pbf
                    .getPropertyBinding(columnsDef.getPropertyName());
            final ColumnDesc<T>[] cds = newCompositBeanColumnDesc(columnNames,
                    pb, columnsDef.getConverter());
            Collections.addAll(list, cds);
        }
    }

    public void setCustomizer(final RecordDefCustomizer columnCustomizer) {
        customizer_ = columnCustomizer;
    }

    private static <U> ColumnDesc<U> newBeanColumnDesc(
            final ColumnName columnName, final PropertyBinding propertyBinding,
            final Converter converter) {
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

        final RecordDef recordDef = new DefaultRecordDef();
        for (final InternalColumnBuilder builder : builders) {
            if (builder.isMultipleColumns()) {
                recordDef.addColumnsDef(builder.getColumnsDef());
            } else {
                recordDef.addColumnDef(builder.getColumnDef());
            }
        }

        final List<ColumnDesc<U>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list, pbf);
        appendColumnDescFromColumnsDef(recordDef, list, pbf);
        final ColumnDesc<U>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    static class DefaultRecordDef implements RecordDef {

        final List<CsvColumnDef> columnDefs_ = CollectionsUtil.newArrayList();
        final List<CsvColumnsDef> columnsDefs_ = CollectionsUtil.newArrayList();

        public boolean isEmpty() {
            return getColumnDefs().isEmpty() && getColumnsDefs().isEmpty();
        }

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
