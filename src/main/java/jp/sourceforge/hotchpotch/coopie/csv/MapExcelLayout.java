package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class MapExcelLayout<PROP> extends AbstractMapCsvLayout<PROP> implements
        ExcelInOut<Map<String, PROP>> {

    @Override
    public RecordReader<Map<String, PROP>> openReader(final InputStream is) {
        prepareOpen();
        final DefaultExcelReader<Map<String, PROP>> r = new DefaultExcelReader<Map<String, PROP>>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementReaderHandler(getElementReaderHandler());

        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public RecordWriter<Map<String, PROP>> openWriter(final OutputStream os) {
        prepareOpen();
        final DefaultExcelWriter<Map<String, PROP>> w = new DefaultExcelWriter<Map<String, PROP>>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

}
