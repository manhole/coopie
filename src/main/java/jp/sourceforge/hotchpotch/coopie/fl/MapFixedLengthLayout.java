package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.ElementInOut;
import jp.sourceforge.hotchpotch.coopie.csv.MapPropertyBinding;
import jp.sourceforge.hotchpotch.coopie.csv.MapRecordType;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBindingFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordInOut;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordType;
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
            final FixedLengthRecordDef recordDef = getRecordDef();
            if (recordDef != null) {
                {
                    final FixedLengthElementDesc[] elementDescs = recordDefToElementDescs(recordDef);
                    setFixedLengthElementDescs(elementDescs);
                }

                final RecordDesc<Map<String, PROP>> recordDesc = createRecordDesc(recordDef);
                setRecordDesc(recordDesc);
            }
        }

        if (getRecordDesc() == null) {
            throw new AssertionError("recordDesc");
        }
    }

    @Override
    protected PropertyBindingFactory<Map<String, PROP>> createPropertyBindingFactory() {
        return MapPropertyBinding.Factory.getInstance();
    }

    @Override
    protected RecordType<Map<String, PROP>> createRecordType() {
        return new MapRecordType<PROP>();
    }

}
