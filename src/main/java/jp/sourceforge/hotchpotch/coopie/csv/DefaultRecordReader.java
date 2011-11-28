package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultRecordReader<T> extends AbstractRecordReader<T> {

    private ElementStream elementStream_;

    public DefaultRecordReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementStream(final ElementStream elementStream) {
        elementStream_ = elementStream;
    }

    public void open(final Readable readable) {
        final ElementReader elementReader = elementStream_.openReader(readable);
        setElementReader(elementReader);
        setClosed(false);

        setupByHeader();
    }

}
