package jp.sourceforge.hotchpotch.coopie.csv;

class DefaultRecordWriter<T> extends AbstractRecordWriter<T> {

    private ElementSetting elementSetting_;

    public DefaultRecordWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementSetting(final ElementSetting elementSetting) {
        elementSetting_ = elementSetting;
    }

    public void open(final Appendable appendable) {
        final ElementWriter elementWriter = elementSetting_
                .openWriter(appendable);
        setElementWriter(elementWriter);
        setClosed(false);
    }

}
