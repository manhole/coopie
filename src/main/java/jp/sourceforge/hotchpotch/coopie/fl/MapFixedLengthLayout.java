package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.InternalColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.ElementInOut;
import jp.sourceforge.hotchpotch.coopie.csv.MapPropertyBinding;
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
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        prepareOpen();
        final RecordDesc<Map<String, PROP>> rd = getRecordDesc();
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
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        prepareOpen();
        final RecordDesc<Map<String, PROP>> rd = getRecordDesc();
        final ElementInOut es = createElementInOut();
        final DefaultRecordWriter<Map<String, PROP>> w = new DefaultRecordWriter<Map<String, PROP>>(
                rd);
        w.setWithHeader(isWithHeader());
        w.setElementInOut(es);
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
    }

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            throw new IllegalStateException("recordDesc");
        }
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
                final List<InternalColumnBuilder> builders) {
            final MapPropertyBinding.Factory pbf = MapPropertyBinding.Factory
                    .getInstance();
            final ColumnDesc<Map<String, PROP>>[] cds = (ColumnDesc[]) AbstractBeanCsvLayout
                    .toColumnDescs(builders, pbf);
            return cds;
        }

    }

}
