package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Writer;

class DefaultRecordWriter<T> extends AbstractRecordWriter<T> {

    private ElementSetting elementSetting_;

    public DefaultRecordWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementSetting(final ElementSetting elementSetting) {
        elementSetting_ = elementSetting;
    }

    public void open(final Writer writer) {
        final ElementWriter elementWriter = elementSetting_.openWriter(writer);
        setElementWriter(elementWriter);
        setClosed(false);
    }

}
