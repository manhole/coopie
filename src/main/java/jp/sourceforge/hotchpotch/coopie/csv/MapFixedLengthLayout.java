package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public class MapFixedLengthLayout extends
        AbstractFixedLengthLayout<Map<String, String>> implements
        CsvLayout<Map<String, String>> {

    @Override
    public RecordReader<Map<String, String>> openReader(final Reader reader) {
        final RecordDesc<Map<String, String>> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultCsvReader<Map<String, String>> r = new DefaultCsvReader<Map<String, String>>(
                rd);
        r.setWithHeader(withHeader);
        r.setElementSetting(es);
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public RecordWriter<Map<String, String>> openWriter(final Writer writer) {
        final RecordDesc<Map<String, String>> rd = getRecordDesc();
        final ElementSetting es = getElementSetting();
        final DefaultCsvWriter<Map<String, String>> w = new DefaultCsvWriter<Map<String, String>>(
                rd);
        w.setWithHeader(withHeader);
        w.setElementSetting(es);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

    @Override
    protected FixedLengthRecordDescSetup getRecordDescSetup() {
        return new MapFixedLengthColumnSetup();
    }

    private static class MapFixedLengthColumnSetup extends
            AbstractFixedLengthColumnSetup<Map<String, String>> {

        private FixedLengthRecordDesc<Map<String, String>> fixedLengthRecordDesc;

        @Override
        public RecordDesc<Map<String, String>> getRecordDesc() {
            buildIfNeed();
            return fixedLengthRecordDesc;
        }

        @Override
        public ElementSetting getElementSetting() {
            buildIfNeed();
            return fixedLengthRecordDesc;
        }

        private void buildIfNeed() {
            if (fixedLengthRecordDesc != null) {
                return;
            }

            /*
             * 設定されているプロパティ名を対象に。
             */

            final ColumnDesc<Map<String, String>>[] cds = AbstractMapCsvLayout
                    .toColumnDescs(columns);

            final FixedLengthColumn[] a = columns
                    .toArray(new FixedLengthColumn[columns.size()]);
            fixedLengthRecordDesc = new FixedLengthRecordDesc<Map<String, String>>(
                    cds, new AbstractMapCsvLayout.MapRecordType(), a);
        }
    }

}
