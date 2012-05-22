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

public class MapFixedLengthLayout<PROP> extends
        AbstractFixedLengthLayout<Map<String, PROP>> implements
        RecordInOut<Map<String, PROP>> {

    @Override
    public RecordReader<Map<String, PROP>> openReader(final Readable readable) {
        final RecordDesc<Map<String, PROP>> rd = myRecordDesc();
        final DefaultRecordReader<Map<String, PROP>> r = new DefaultRecordReader<Map<String, PROP>>(
                rd);
        r.setWithHeader(isWithHeader());
        r.setElementInOut(createElementInOut());
        // TODO openで例外時にcloseすること
        r.open(readable);
        return r;
    }

    @Override
    public RecordWriter<Map<String, PROP>> openWriter(
            final Appendable appendable) {
        final RecordDesc<Map<String, PROP>> rd = myRecordDesc();
        final ElementInOut es = createElementInOut();
        final DefaultRecordWriter<Map<String, PROP>> w = new DefaultRecordWriter<Map<String, PROP>>(
                rd);
        w.setWithHeader(isWithHeader());
        w.setElementInOut(es);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    protected RecordDesc<Map<String, PROP>> myRecordDesc() {
        final RecordDesc<Map<String, PROP>> recordDesc = super.getRecordDesc();
        if (recordDesc == null) {
            throw new IllegalStateException("recordDesc");
        }
        return recordDesc;
    }

    @Override
    protected FixedLengthRecordDescSetup getRecordDescSetup() {
        return new MapFixedLengthRecordDescSetup<PROP>();
    }

    private static class MapFixedLengthRecordDescSetup<PROP> extends
            AbstractFixedLengthRecordDescSetup<Map<String, PROP>> {

        private final MapRecordType<PROP> recordType_ = new MapRecordType<PROP>();

        @Override
        protected MapRecordType<PROP> getRecordType() {
            return recordType_;
        }

        @Override
        protected ColumnDesc<Map<String, PROP>>[] createColumnDescs(
                final List<SimpleColumnBuilder> builders) {
            final ColumnDesc<Map<String, PROP>>[] cds = AbstractMapCsvLayout
                    .toColumnDescs(builders);
            return cds;
        }

    }

}
