package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.SimpleColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractMapCsvLayout;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.ElementInOut;
import jp.sourceforge.hotchpotch.coopie.csv.MapRecordType;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordInOut;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;

public class MapFixedLengthLayout extends
        AbstractFixedLengthLayout<Map<String, String>> implements
        RecordInOut<Map<String, String>> {

    @Override
    public RecordReader<Map<String, String>> openReader(final Readable readable) {
        final RecordDesc<Map<String, String>> rd = myRecordDesc();
        final DefaultRecordReader<Map<String, String>> r = new DefaultRecordReader<Map<String, String>>(
                rd);
        r.setWithHeader(isWithHeader());
        r.setElementInOut(createElementInOut());
        // TODO openで例外時にcloseすること
        r.open(readable);
        return r;
    }

    @Override
    public RecordWriter<Map<String, String>> openWriter(
            final Appendable appendable) {
        final RecordDesc<Map<String, String>> rd = myRecordDesc();
        final ElementInOut es = createElementInOut();
        final DefaultRecordWriter<Map<String, String>> w = new DefaultRecordWriter<Map<String, String>>(
                rd);
        w.setWithHeader(isWithHeader());
        w.setElementInOut(es);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    protected RecordDesc<Map<String, String>> myRecordDesc() {
        final RecordDesc<Map<String, String>> recordDesc = super
                .getRecordDesc();
        if (recordDesc == null) {
            throw new IllegalStateException("recordDesc");
        }
        return recordDesc;
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
                final List<SimpleColumnBuilder> builders) {
            final ColumnDesc<Map<String, String>>[] cds = AbstractMapCsvLayout
                    .toColumnDescs(builders);
            return cds;
        }

    }

}
