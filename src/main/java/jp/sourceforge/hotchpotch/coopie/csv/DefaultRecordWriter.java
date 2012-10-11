package jp.sourceforge.hotchpotch.coopie.csv;

public class DefaultRecordWriter<BEAN> extends AbstractRecordWriter<BEAN> {

    private ElementInOut elementInOut_;

    public DefaultRecordWriter(final RecordDesc<BEAN> recordDesc) {
        super(recordDesc);
    }

    public void setElementInOut(final ElementInOut elementInOut) {
        elementInOut_ = elementInOut;
    }

    public void open(final Appendable appendable) {
        final ElementWriter elementWriter = elementInOut_
                .openWriter(appendable);
        setElementWriter(elementWriter);
        setClosed(false);
    }

}
