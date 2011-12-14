package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultRecordReader<T> extends AbstractRecordReader<T> {

    private ElementInOut elementInOut_;

    public DefaultRecordReader(final RecordDesc<T> recordDesc) {
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
