package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.MethodDesc;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

public abstract class AbstractBeanCsvLayout<T> extends AbstractCsvLayout<T> {

    private final BeanDesc<T> beanDesc_;

    private CsvRecordDefCustomizer customizer_ = EmptyRecordDefCustomizer
            .getInstance();

    public AbstractBeanCsvLayout(final Class<T> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            final CsvRecordDef recordDef = recordDef();
            customizer_.customize(recordDef);
            final PropertyBindingFactory<T> pbf = new BeanPropertyBinding.Factory<T>(
                    beanDesc_);
            final ColumnDesc<T>[] cds = recordDefToColumnDesc(recordDef, pbf);
            // TODO アノテーションのorderが全て指定されていた場合はSPECIFIEDにするべきでは?
            final RecordDesc<T> recordDesc = new DefaultRecordDesc<T>(cds,
                    recordDef.getOrderSpecified(), new BeanRecordType<T>(
                            beanDesc_));
            setRecordDesc(recordDesc);
        }

        if (getRecordDesc() == null) {
            throw new AssertionError();
        }
    }

    private CsvRecordDef recordDef() {
        if (getRecordDef() == null) {
            final CsvRecordDef r = createRecordDef();
            setRecordDef(r);
        }
        return getRecordDef();
    }

    private CsvRecordDef createRecordDef() {
        /*
         * アノテーションが付いている場合は、アノテーションを優先する
         */
        CsvRecordDef recordDef = createRecordDefByAnnotation();
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

    static <T> ColumnDesc<T>[] recordDefToColumnDesc(
            final CsvRecordDef recordDef, final PropertyBindingFactory<T> pbf) {
        final List<ColumnDesc<T>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list, pbf);
        appendColumnDescFromColumnsDef(recordDef, list, pbf);
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private CsvRecordDef createRecordDefByAnnotation() {
        final DefaultCsvRecordDef recordDef = new DefaultCsvRecordDef();
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

    private CsvRecordDef createRecordDefByProperties() {
        final DefaultCsvRecordDef recordDef = new DefaultCsvRecordDef();
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
            final CsvRecordDef recordDef, final List<ColumnDesc<T>> list,
            final PropertyBindingFactory<T> pbf) {
        for (final CsvColumnDef columnDef : recordDef.getColumnDefs()) {
            final ColumnName columnName = columnDef.getColumnName();
            final PropertyBinding<T, Object> pb = pbf
                    .getPropertyBinding(columnDef.getPropertyName());
            final ColumnDesc<T> cd = DefaultColumnDesc.newColumnDesc(
                    columnName, pb, columnDef.getConverter());
            list.add(cd);
        }
    }

    private static <T> void appendColumnDescFromColumnsDef(
            final CsvRecordDef recordDef, final List<ColumnDesc<T>> list,
            final PropertyBindingFactory<T> pbf) {
        for (final CsvColumnsDef columnsDef : recordDef.getColumnsDefs()) {
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            for (final CsvColumnDef columnDef : columnsDef.getColumnDefs()) {
                columnNames.add(columnDef.getColumnName());
            }
            final PropertyBinding<T, Object> pb = pbf
                    .getPropertyBinding(columnsDef.getPropertyName());
            final ColumnDesc<T>[] cds = CompositColumnDesc
                    .newCompositColumnDesc(columnNames, pb,
                            columnsDef.getConverter());
            Collections.addAll(list, cds);
        }
    }

    public void setCustomizer(final CsvRecordDefCustomizer columnCustomizer) {
        customizer_ = columnCustomizer;
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

        public static <T> ColumnDesc<T>[] newCompositColumnDesc(
                final List<ColumnName> names,
                final PropertyBinding<T, Object> propertyBinding,
                final Converter converter) {

            final CompositColumnDesc ccd = new CompositColumnDesc();
            ccd.setPropertyBinding(propertyBinding);
            ccd.setColumnNames(names);
            ccd.setConverter(converter);
            return ccd.getColumnDescs();
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

    public static class DefaultColumnDesc<T> implements ColumnDesc<T> {

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

        public static <T> ColumnDesc<T> newColumnDesc(
                final ColumnName columnName,
                final PropertyBinding propertyBinding, final Converter converter) {
            final DefaultColumnDesc<T> cd = new DefaultColumnDesc<T>();
            cd.setName(columnName);
            cd.setPropertyBinding(propertyBinding);
            cd.setConverter(converter);
            return cd;
        }

    }

}
