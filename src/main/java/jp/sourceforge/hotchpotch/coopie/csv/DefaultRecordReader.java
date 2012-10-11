package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultRecordReader<BEAN> extends AbstractRecordReader<BEAN> {

    private ElementInOut elementInOut_;

    public DefaultRecordReader(final RecordDesc<BEAN> recordDesc) {
        super(recordDesc);
    }

    public void setElementInOut(final ElementInOut elementInOut) {
        elementInOut_ = elementInOut;
    }

    public void open(final Readable readable) {
        final ElementReader elementReader = elementInOut_.openReader(readable);
        setElementReader(elementReader);
        setClosed(false);

        setupByHeader();
    }

}
