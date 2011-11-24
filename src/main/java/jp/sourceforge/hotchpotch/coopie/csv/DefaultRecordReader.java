package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;

class DefaultRecordReader<T> extends AbstractRecordReader<T> {

    private ElementSetting elementSetting_;

    public DefaultRecordReader(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementSetting(final ElementSetting elementSetting) {
        elementSetting_ = elementSetting;
    }

    public void open(final Reader reader) {
        final ElementReader elementReader = elementSetting_.openReader(reader);
        setElementReader(elementReader);
        setClosed(false);

        setupByHeader();
    }

}
