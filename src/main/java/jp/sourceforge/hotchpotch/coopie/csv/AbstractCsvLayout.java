package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.slf4j.Logger;

abstract class AbstractCsvLayout<T> extends AbstractLayout<T> {

    private static final Logger logger = LoggerFactory.getLogger();
    protected RecordDesc<T> recordDesc;
    protected boolean withHeader = true;

    public void setupColumns(final SetupBlock<ColumnSetup> block) {
        final RecordDescSetup<T> columnSetup = getRecordDescSetup();
        block.setup(columnSetup);
        recordDesc = columnSetup.getRecordDesc();
    }

    protected abstract RecordDescSetup<T> getRecordDescSetup();

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

}
