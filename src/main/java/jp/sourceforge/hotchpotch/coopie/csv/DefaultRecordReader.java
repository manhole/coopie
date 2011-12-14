package jp.sourceforge.hotchpotch.coopie.csv;

class DefaultRecordReader<T> extends AbstractRecordReader<T> {

    private ElementSetting elementSetting_;

    public DefaultRecordReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementSetting(final ElementSetting elementSetting) {
        elementSetting_ = elementSetting;
    }

    public void open(final Readable readable) {
        final ElementReader elementReader = elementSetting_
                .openReader(readable);
        setElementReader(elementReader);
        setClosed(false);

        setupByHeader();
    }

}
