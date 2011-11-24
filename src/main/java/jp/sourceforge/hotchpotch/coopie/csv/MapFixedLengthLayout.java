package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;
import java.util.Map;

public class MapFixedLengthLayout extends
        AbstractFixedLengthLayout<Map<String, String>> implements
        CsvLayout<Map<String, String>> {

    @Override
    public RecordReader<Map<String, String>> openReader(final Readable readable) {
        final RecordDesc<Map<String, String>> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultRecordReader<Map<String, String>> r = new DefaultRecordReader<Map<String, String>>(
                rd);
        r.setWithHeader(isWithHeader());
        r.setElementSetting(es);
        // TODO openで例外時にcloseすること
        r.open(readable);
        return r;
    }

    @Override
    public RecordWriter<Map<String, String>> openWriter(
            final Appendable appendable) {
        final RecordDesc<Map<String, String>> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultRecordWriter<Map<String, String>> w = new DefaultRecordWriter<Map<String, String>>(
                rd);
        w.setWithHeader(isWithHeader());
        w.setElementSetting(es);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    @Override
    protected FixedLengthRecordDescSetup getRecordDescSetup() {
        return new MapFixedLengthRecordDescSetup();
    }

    private static class MapFixedLengthRecordDescSetup extends
            AbstractFixedLengthRecordDescSetup<Map<String, String>> {

        private final MapRecordType recordType_ = new MapRecordType();

        @Override
        protected MapRecordType getRecordType() {
            return recordType_;
        }

        @Override
        protected ColumnDesc<Map<String, String>>[] createColumnDescs(
                final List<ColumnName> columnNames) {
            final ColumnDesc<Map<String, String>>[] cds = AbstractMapCsvLayout
                    .toColumnDescs(columnNames);
            return cds;
        }

    }

}
