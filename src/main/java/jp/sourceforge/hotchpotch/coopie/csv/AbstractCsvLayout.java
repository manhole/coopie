package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractRecordReader.ReadEditor;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractCsvLayout<T> {

    private static final Logger logger = LoggerFactory.getLogger();
    private RecordDesc<T> recordDesc_;
    private boolean withHeader_ = true;
    private ReadEditor readEditor_;

    public void setupColumns(final SetupBlock<CsvColumnSetup> block) {
        final CsvRecordDescSetup<T> setup = getRecordDescSetup();
        block.setup(setup);
        recordDesc_ = setup.getRecordDesc();
    }

    protected abstract CsvRecordDescSetup<T> getRecordDescSetup();

    protected boolean isWithHeader() {
        return withHeader_;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected ReadEditor getReadEditor() {
        return readEditor_;
    }

    public void setReadEditor(final ReadEditor readEditor) {
        readEditor_ = readEditor;
    }

    protected RecordDesc<T> getRecordDesc() {
        return recordDesc_;
    }

    protected void setRecordDesc(final RecordDesc<T> recordDesc) {
        recordDesc_ = recordDesc;
    }

    protected static interface CsvRecordDescSetup<T> extends CsvColumnSetup {

        RecordDesc<T> getRecordDesc();

    }

    protected static abstract class AbstractCsvRecordDescSetup<T> implements
            CsvRecordDescSetup<T> {

        protected final List<ColumnName> columnNames_ = CollectionsUtil
                .newArrayList();

        @Override
        public void column(final ColumnName name) {
            columnNames_.add(name);
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
