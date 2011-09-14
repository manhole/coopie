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
            final SimpleColumnBuilder builder = new SimpleColumnBuilder(name);
            columnBuilders.add(builder);
            return builder;
        }

        @Override
        public ColumnBuilder column(final String name) {
            final SimpleColumnName n = new SimpleColumnName(name);
            return column(n);
        }

        @Override
        public ColumnBuilder column(final String propertyName,
                final String label) {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            return column(n);
        }

        protected static class SimpleColumnBuilder implements ColumnBuilder {

            private final ColumnName columnName_;
            private Converter converter_;

            public SimpleColumnBuilder(final ColumnName columnName) {
                columnName_ = columnName;
            }

            @Override
            public void converter(final Converter converter) {
                converter_ = converter;
            }

            public ColumnName getColumnName() {
                return columnName_;
            }

            public Converter getConverter() {
                return converter_;
            }

        }

    }

}
