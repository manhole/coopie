package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractRecordReader.ReadEditor;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractCsvLayout<T> {

    private static final Logger logger = LoggerFactory.getLogger();
    protected RecordDesc<T> recordDesc;
    protected boolean withHeader = true;
    protected ReadEditor readEditor;

    public void setupColumns(final SetupBlock<CsvColumnSetup> block) {
        final CsvRecordDescSetup<T> setup = getRecordDescSetup();
        block.setup(setup);
        recordDesc = setup.getRecordDesc();
    }

    protected abstract CsvRecordDescSetup<T> getRecordDescSetup();

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

    public void setReadEditor(final ReadEditor readEditor) {
        this.readEditor = readEditor;
    }

    protected static interface CsvRecordDescSetup<T> extends CsvColumnSetup {

        RecordDesc<T> getRecordDesc();

    }

    protected static abstract class AbstractCsvRecordDescSetup<T> implements
            CsvRecordDescSetup<T> {

        protected final List<SimpleColumnBuilder> columnBuilders = CollectionsUtil
                .newArrayList();

        @Override
        public ColumnBuilder column(final ColumnName name) {
            final SimpleColumnBuilder builder = builder(name);
            builder.property(name.getLabel());
            return builder;
        }

        @Override
        public ColumnBuilder column(final String name) {
            final SimpleColumnName n = new SimpleColumnName(name);
            final SimpleColumnBuilder builder = builder(n);
            builder.property(name);
            return builder;
        }

        @Override
        public ColumnBuilder column(final String propertyName,
                final String label) {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            final SimpleColumnBuilder builder = builder(n);
            builder.property(propertyName);
            return builder;
        }

        private SimpleColumnBuilder builder(final ColumnName name) {
            final SimpleColumnBuilder builder = new SimpleColumnBuilder(name);
            columnBuilders.add(builder);
            return builder;
        }

        @Override
        public ColumnBuilder columns(final String... names) {
            final SimpleColumnBuilder builder = new SimpleColumnBuilder();
            columnBuilders.add(builder);
            for (final String name : names) {
                final SimpleColumnName n = new SimpleColumnName(name);
                builder.addColumnName(n);
            }
            return builder;
        }

        protected static class SimpleColumnBuilder implements ColumnBuilder {

            private final List<ColumnName> columnNames_ = CollectionsUtil
                    .newArrayList();
            private Converter converter_;
            private final List<String> propertyNames_ = CollectionsUtil
                    .newArrayList();

            public SimpleColumnBuilder() {
            }

            public SimpleColumnBuilder(final ColumnName columnName) {
                addColumnName(columnName);
            }

            public void addColumnName(final ColumnName columnName) {
                columnNames_.add(columnName);
            }

            @Override
            public ColumnBuilder property(final String propertyName) {
                propertyNames_.add(propertyName);
                return this;
            }

            @Override
            public void converter(final Converter converter) {
                converter_ = converter;
            }

            public ColumnName getColumnName() {
                if (columnNames_.isEmpty()) {
                    return null;
                }
                return columnNames_.get(0);
            }

            public List<ColumnName> getColumnNames() {
                return columnNames_;
            }

            public Converter getConverter() {
                return converter_;
            }

            public String getPropertyName() {
                if (propertyNames_.isEmpty()) {
                    return null;
                }
                return propertyNames_.get(0);
            }

            public List<String> getPropertyNames() {
                return propertyNames_;
            }

            public void setPropertyName(final String propertyName) {
                propertyNames_.clear();
                propertyNames_.add(propertyName);
            }

        }

    }

}
