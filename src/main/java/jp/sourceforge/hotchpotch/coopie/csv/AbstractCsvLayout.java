package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup.ColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public abstract class AbstractCsvLayout<T> {

    private static final Logger logger = LoggerFactory.getLogger();
    private RecordDesc<T> recordDesc_;
    private boolean withHeader_ = true;
    private ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler
            .getInstance();
    private LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();
    private ElementEditor elementEditor_;

    public void setupColumns(final SetupBlock<CsvColumnSetup> block) {
        final CsvRecordDescSetup<T> setup = getRecordDescSetup();
        block.setup(setup);
        recordDesc_ = setup.getRecordDesc();
    }

    protected abstract CsvRecordDescSetup<T> getRecordDescSetup();

    protected RecordDesc<T> getRecordDesc() {
        return recordDesc_;
    }

    protected void setRecordDesc(final RecordDesc<T> recordDesc) {
        recordDesc_ = recordDesc;
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

    protected static interface CsvRecordDescSetup<T> extends CsvColumnSetup {

        RecordDesc<T> getRecordDesc();

    }

    protected static abstract class AbstractCsvRecordDescSetup<T> implements
            CsvRecordDescSetup<T> {

        private final List<InternalColumnBuilder> columnBuilders_ = CollectionsUtil
                .newArrayList();

        @Override
        public ColumnBuilder column(final ColumnName name) {
            final InternalColumnBuilder builder = builder(name);
            builder.toProperty(name.getLabel());
            return builder;
        }

        @Override
        public ColumnBuilder column(final String name) {
            final SimpleColumnName n = new SimpleColumnName(name);
            final InternalColumnBuilder builder = builder(n);
            return builder;
        }

        private InternalColumnBuilder builder(final ColumnName name) {
            final SimpleColumnBuilder builder = new SimpleColumnBuilder(name);
            columnBuilders_.add(builder);
            return builder;
        }

        @Override
        public ColumnBuilder columns(final String... names) {
            final SimpleColumnBuilder builder = new SimpleColumnBuilder();
            columnBuilders_.add(builder);
            for (final String name : names) {
                final SimpleColumnName n = new SimpleColumnName(name);
                builder.addColumnName(n);
            }
            return builder;
        }

        protected List<InternalColumnBuilder> getColumnBuilders() {
            return columnBuilders_;
        }

    }

    public interface InternalColumnBuilder extends ColumnBuilder {

        List<ColumnName> getColumnNames();

        List<String> getPropertyNames();

        Converter getConverter();

    }

    public static class SimpleColumnBuilder implements InternalColumnBuilder {

        private final List<ColumnName> columnNames_ = CollectionsUtil
                .newArrayList();
        private final List<String> propertyNames_ = CollectionsUtil
                .newArrayList();
        private Converter converter_ = PassthroughStringConverter.getInstance();

        public SimpleColumnBuilder() {
        }

        public SimpleColumnBuilder(final ColumnName columnName) {
            addColumnName(columnName);
        }

        @Override
        public void withConverter(final Converter converter) {
            converter_ = converter;
        }

        public void addColumnName(final ColumnName columnName) {
            columnNames_.add(columnName);
        }

        @Override
        public List<ColumnName> getColumnNames() {
            return columnNames_;
        }

        @Override
        public ColumnBuilder toProperty(final String propertyName) {
            propertyNames_.add(propertyName);
            return this;
        }

        @Override
        public Converter getConverter() {
            return converter_;
        }

        @Override
        public List<String> getPropertyNames() {
            return propertyNames_;
        }

    }

}
