package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Writer;

class DefaultCsvWriter<T> extends AbstractCsvWriter<T> {

    private ElementSetting elementSetting;

    public DefaultCsvWriter(final RecordDesc<T> recordDesc) {
        super(recordDesc);
    }

    public void setElementSetting(final ElementSetting elementSetting) {
        this.elementSetting = elementSetting;
    }

    public void open(final Writer writer) {
        elementWriter = elementSetting.openWriter(writer);
        closed = false;
    }

}
