package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvReader.CustomLayout;

public class BeanCsvLayout<T> extends AbstractBeanCsvLayout<T> implements
        CsvLayout<T> {

    private final CsvSetting csvSetting = new CsvSetting();
    private CustomLayout customLayout;

    public BeanCsvLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public CsvReader<T> openReader(final Reader reader) {
        final DefaultCsvReader<T> r = new DefaultCsvReader<T>(getRecordDesc());
        r.setWithHeader(withHeader);
        r.setElementSetting(csvSetting);
        if (customLayout != null) {
            r.setCustomLayout(customLayout);
        }
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public CsvWriter<T> openWriter(final Writer writer) {
        final DefaultCsvWriter<T> w = new DefaultCsvWriter<T>(getRecordDesc());
        w.setWithHeader(withHeader);
        w.setElementSetting(csvSetting);
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

    public void setCustomLayout(final CustomLayout customLayout) {
        this.customLayout = customLayout;
    }

}
