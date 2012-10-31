package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Collections;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup.ColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup.CompositeColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

public abstract class AbstractCsvLayout<BEAN> {

    private static final Logger logger = LoggerFactory.getLogger();
    private RecordDesc<BEAN> recordDesc_;
    private CsvRecordDef recordDef_;
    private PropertyBindingFactory<BEAN> propertyBindingFactory_;
    private RecordType<BEAN> recordType_;
    private ConverterRepository converterRepository_ = NullConverterRepository
            .getInstance();

    private boolean withHeader_ = true;
    private ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler
            .getInstance();
    private LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();
    private ElementEditor elementEditor_;

    public void setupColumns(final SetupBlock<CsvColumnSetup> block) {
        recordDesc_ = null;
        final CsvRecordDefSetup setup = getRecordDefSetup();
        block.setup(setup);
        recordDef_ = setup.getRecordDef();
    }

    protected CsvRecordDefSetup getRecordDefSetup() {
        return new DefaultCsvRecordDefSetup();
    }

    protected RecordDesc<BEAN> getRecordDesc() {
        return recordDesc_;
    }

    protected void setRecordDesc(final RecordDesc<BEAN> recordDesc) {
        recordDesc_ = recordDesc;
    }

    protected CsvRecordDef getRecordDef() {
        return recordDef_;
    }

    protected void setRecordDef(final CsvRecordDef recordDef) {
        recordDef_ = recordDef;
    }

    protected boolean isWithHeader() {
        return withHeader_;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected LineReaderHandler getLineReaderHandler() {
        return lineReaderHandler_;
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

    protected ElementReaderHandler getElementReaderHandler() {
        return elementReaderHandler_;
    }

    public void setElementReaderHandler(
            final ElementReaderHandler elementReaderHandler) {
        elementReaderHandler_ = elementReaderHandler;
    }

    protected ElementEditor getElementEditor() {
        return elementEditor_;
    }

    public void setElementEditor(final ElementEditor elementEditor) {
        elementEditor_ = elementEditor;
    }

    /**
     * カスタマイズ用hander実装をまとめて登録する、コンビニエンスメソッドです。
     * 
     * @param handler {@link LineReaderHandler} {@link ElementReaderHandler}
     *  {@link ElementEditor} の1つ以上をimplementsしたインスタンス
     * @exception IllegalArgumentException 上記インタフェースを1つもimplementsしていない場合
     * 
     * @see #setLineReaderHandler(LineReaderHandler)
     * @see #setElementReaderHandler(ElementReaderHandler)
     * @see #setElementEditor(ElementEditor)
     */
    public void setReaderHandler(final Object handler) {
        int assigned = 0;
        if (handler instanceof LineReaderHandler) {
            setLineReaderHandler(LineReaderHandler.class.cast(handler));
            assigned++;
        }
        if (handler instanceof ElementReaderHandler) {
            setElementReaderHandler(ElementReaderHandler.class.cast(handler));
            assigned++;
        }
        if (handler instanceof ElementEditor) {
            setElementEditor(ElementEditor.class.cast(handler));
            assigned++;
        }

        /*
         * いずれもsetできない場合は例外にする。
         */
        if (assigned == 0) {
            throw new IllegalArgumentException("no suitable");
        }
    }

    protected ConverterRepository getConverterRepository() {
        return converterRepository_;
    }

    public void setConverterRepository(
            final ConverterRepository converterRepository) {
        converterRepository_ = converterRepository;
    }

    protected RecordDesc<BEAN> createRecordDesc(final CsvRecordDef recordDef) {
        final PropertyBindingFactory<BEAN> pbf = getPropertyBindingFactory();
        final RecordType<BEAN> recordType = getRecordType();
        final ColumnDesc<BEAN>[] cds = recordDefToColumnDesc(recordDef, pbf);
        // TODO アノテーションのorderが全て指定されていた場合はSPECIFIEDにするべきでは?
        final RecordDesc<BEAN> recordDesc = new DefaultRecordDesc<BEAN>(cds,
                recordDef.getOrderSpecified(), recordType);
        return recordDesc;
    }

    private ColumnDesc<BEAN>[] recordDefToColumnDesc(
            final CsvRecordDef recordDef, final PropertyBindingFactory<BEAN> pbf) {

        final List<ColumnDesc<BEAN>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list, pbf);
        appendColumnDescFromColumnsDef(recordDef, list, pbf);
        final ColumnDesc<BEAN>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private void appendColumnDescFromColumnDef(final CsvRecordDef recordDef,
            final List<ColumnDesc<BEAN>> list,
            final PropertyBindingFactory<BEAN> pbf) {

        for (final CsvColumnDef columnDef : recordDef.getColumnDefs()) {
            final ColumnName columnName = newColumnName(columnDef);
            final PropertyBinding<BEAN, Object> pb = pbf
                    .getPropertyBinding(columnDef.getPropertyName());
            final ColumnDesc<BEAN> cd = DefaultColumnDesc.newColumnDesc(
                    columnName, pb, columnDef.getConverter());
            list.add(cd);
        }
    }

    private void appendColumnDescFromColumnsDef(final CsvRecordDef recordDef,
            final List<ColumnDesc<BEAN>> list,
            final PropertyBindingFactory<BEAN> pbf) {

        for (final CsvColumnsDef columnsDef : recordDef.getColumnsDefs()) {
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            for (final CsvColumnDef columnDef : columnsDef.getColumnDefs()) {
                columnNames.add(newColumnName(columnDef));
            }
            final PropertyBinding<BEAN, Object> pb = pbf
                    .getPropertyBinding(columnsDef.getPropertyName());
            final ColumnDesc<BEAN>[] cds = CompositeColumnDesc
                    .newCompositeColumnDesc(columnNames, pb,
                            columnsDef.getConverter());
            Collections.addAll(list, cds);
        }
    }

    private ColumnName newColumnName(final CsvColumnDef columnDef) {
        final SimpleColumnName columnName = new SimpleColumnName(
                columnDef.getLabel());
        columnName.setColumnNameMatcher(columnDef.getColumnNameMatcher());
        return columnName;
    }

    protected PropertyBindingFactory<BEAN> getPropertyBindingFactory() {
        if (propertyBindingFactory_ == null) {
            propertyBindingFactory_ = createPropertyBindingFactory();
        }
        return propertyBindingFactory_;
    }

    protected abstract PropertyBindingFactory<BEAN> createPropertyBindingFactory();

    protected RecordType<BEAN> getRecordType() {
        if (recordType_ == null) {
            recordType_ = createRecordType();
        }
        return recordType_;
    }

    protected abstract RecordType<BEAN> createRecordType();

    protected static interface CsvRecordDefSetup extends CsvColumnSetup {

        CsvRecordDef getRecordDef();

    }

    protected static class DefaultCsvRecordDefSetup implements
            CsvRecordDefSetup {

        private final List columnBuilders_ = CollectionsUtil.newArrayList();

        private CsvRecordDef recordDef_;

        @Override
        public ColumnBuilder column(final String name) {
            final DefaultCsvColumnDef def = new DefaultCsvColumnDef();
            def.setLabel(name);
            def.setPropertyName(name);
            final InternalCsvColumnBuilder builder = builder(def);
            return builder;
        }

        @Override
        public ColumnBuilder column(final CsvColumnDef columnDef) {
            final InternalCsvColumnBuilder builder = builder(columnDef);
            return builder;
        }

        private InternalCsvColumnBuilder builder(final CsvColumnDef columnDef) {
            final CsvColumnBuilder builder = new CsvColumnBuilder(columnDef);
            columnBuilders_.add(builder);
            return builder;
        }

        @Override
        public CompositeColumnBuilder columns(
                final SetupBlock<CsvCompositeColumnSetup> compositeSetup) {

            final DefaultCsvColumnsDef columnsDef = new DefaultCsvColumnsDef();
            final CsvCompositeColumnBuilder builder = new CsvCompositeColumnBuilder(
                    columnsDef);
            compositeSetup.setup(new CsvCompositeColumnSetup() {
                @Override
                public ColumnBuilder column(final String name) {
                    final DefaultCsvColumnDef def = new DefaultCsvColumnDef();
                    def.setLabel(name);
                    def.setPropertyName(name);
                    final CsvColumnBuilder builder = new CsvColumnBuilder(def);
                    columnsDef.addColumnDef(def);
                    return builder;
                }
            });
            columnBuilders_.add(builder);
            return builder;
        }

        @Override
        public CsvRecordDef getRecordDef() {
            buildIfNeed();
            return recordDef_;
        }

        private void buildIfNeed() {
            if (recordDef_ != null) {
                return;
            }

            /*
             * 設定されているプロパティ名を対象に。
             */
            final CsvRecordDef recordDef = new DefaultCsvRecordDef();
            for (final Object builder : columnBuilders_) {
                if (builder instanceof InternalCsvColumnBuilder) {
                    final CsvColumnDef columnDef = ((InternalCsvColumnBuilder) builder)
                            .getColumnDef();
                    recordDef.addColumnDef(columnDef);
                } else if (builder instanceof InternalCsvCompositeColumnBuilder) {
                    final CsvColumnsDef columnsDef = ((InternalCsvCompositeColumnBuilder) builder)
                            .getCompositeColumnDef();
                    if (StringUtil.isEmpty(columnsDef.getPropertyName())) {
                        final List<String> names = CollectionsUtil
                                .newArrayList();
                        final List<CsvColumnDef> defs = columnsDef
                                .getColumnDefs();
                        for (final CsvColumnDef def : defs) {
                            names.add(def.getLabel());
                        }
                        throw new IllegalStateException(
                                "property is not specified. for column "
                                        + names);
                    }
                    recordDef.addColumnsDef(columnsDef);
                } else {
                    throw new AssertionError();
                }
            }
            recordDef.setOrderSpecified(OrderSpecified.SPECIFIED);
            recordDef_ = recordDef;
        }

    }

    public interface InternalColumnBuilder extends ColumnBuilder {

    }

    public interface InternalCompositeColumnBuilder extends
            CompositeColumnBuilder {

    }

    public interface InternalCsvColumnBuilder extends InternalColumnBuilder {

        CsvColumnDef getColumnDef();

    }

    public interface InternalCsvCompositeColumnBuilder extends
            InternalCompositeColumnBuilder {

        CsvColumnsDef getCompositeColumnDef();

    }

    static class CsvColumnBuilder implements InternalCsvColumnBuilder {

        private final CsvColumnDef columnDef_;

        public CsvColumnBuilder(final CsvColumnDef columnDef) {
            columnDef_ = columnDef;
        }

        @Override
        public CsvColumnDef getColumnDef() {
            return columnDef_;
        }

        @Override
        public ColumnBuilder toProperty(final String propertyName) {
            columnDef_.setPropertyName(propertyName);
            return this;
        }

        @Override
        public ColumnBuilder withConverter(final Converter converter) {
            columnDef_.setConverter(converter);
            return this;
        }

        @Override
        public ColumnBuilder withColumnNameMatcher(
                final ColumnNameMatcher columnNameMatcher) {
            columnDef_.setColumnNameMatcher(columnNameMatcher);
            return this;
        }

    }

    static class CsvCompositeColumnBuilder implements
            InternalCsvCompositeColumnBuilder {

        private final CsvColumnsDef columnsDef_;

        public CsvCompositeColumnBuilder(final CsvColumnsDef columnsDef) {
            columnsDef_ = columnsDef;
        }

        @Override
        public CsvColumnsDef getCompositeColumnDef() {
            return columnsDef_;
        }

        @Override
        public CompositeColumnBuilder toProperty(final String propertyName) {
            columnsDef_.setPropertyName(propertyName);
            return this;
        }

        @Override
        public CompositeColumnBuilder withConverter(final Converter converter) {
            columnsDef_.setConverter(converter);
            return this;
        }

    }

    private static class NullConverterRepository implements ConverterRepository {

        private static final ConverterRepository INSTANCE = new NullConverterRepository();

        public static ConverterRepository getInstance() {
            return INSTANCE;
        }

        @Override
        public Converter detect(final CsvColumnDef columnDef) {
            return null;
        }

    }

}
