package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class MapExcelLayout extends AbstractMapCsvLayout implements
        ExcelInOut<Map<String, String>> {

    @Override
    public RecordReader<Map<String, String>> openReader(final InputStream is) {
        final DefaultExcelReader<Map<String, String>> r = new DefaultExcelReader<Map<String, String>>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public RecordWriter<Map<String, String>> openWriter(final OutputStream os) {
        final DefaultExcelWriter<Map<String, String>> w = new DefaultExcelWriter<Map<String, String>>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

}
