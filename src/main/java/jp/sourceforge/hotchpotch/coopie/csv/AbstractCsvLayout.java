package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractCsvLayout<T> extends AbstractLayout<T> {

    private static final Logger logger = LoggerFactory.getLogger();
    protected RecordDesc<T> recordDesc;
    protected boolean withHeader = true;

    public void setupColumns(final SetupBlock<ColumnSetup> block) {
        final CsvRecordDescSetup<T> columnSetup = getRecordDescSetup();
        block.setup(columnSetup);
        recordDesc = columnSetup.getRecordDesc();
    }

    protected abstract CsvRecordDescSetup<T> getRecordDescSetup();

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

    protected static interface CsvRecordDescSetup<T> extends ColumnSetup {

        RecordDesc<T> getRecordDesc();

    }

    protected static abstract class AbstractColumnSetup<T> implements
            CsvRecordDescSetup<T> {

        protected final List<ColumnName> columnNames = CollectionsUtil
                .newArrayList();

        @Override
        public void column(final ColumnName name) {
            columnNames.add(name);
        }

        @Override
        public void column(final String name) {
            column(new SimpleColumnName(name));
        }

        @Override
        public void column(final String propertyName, final String label) {
            final SimpleColumnName n = new SimpleColumnName();
            n.setName(propertyName);
            n.setLabel(label);
            column(n);
        }

    }

}
