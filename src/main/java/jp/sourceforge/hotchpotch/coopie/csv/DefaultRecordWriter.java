package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultRecordWriter<T> extends AbstractRecordWriter<T> {

    private ElementStream elementStream_;

    public DefaultRecordWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementStream(final ElementStream elementStream) {
        elementStream_ = elementStream;
    }

    public void open(final Appendable appendable) {
        final ElementWriter elementWriter = elementStream_
                .openWriter(appendable);
        setElementWriter(elementWriter);
        setClosed(false);
    }

}
