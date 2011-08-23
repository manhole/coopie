package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class BeanExcelLayout<T> extends AbstractBeanCsvLayout<T> implements
        ExcelLayout<T> {

    private DefaultExcelWriter.WriteEditor writeEditor;

    public BeanExcelLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public CsvReader<T> openReader(final InputStream is) {
        final DefaultExcelReader<T> r = new DefaultExcelReader<T>(
                getRecordDesc());
        r.setWithHeader(withHeader);
        if (customLayout != null) {
            r.setCustomLayout(customLayout);
        }
        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public CsvWriter<T> openWriter(final OutputStream os) {
        final DefaultExcelWriter<T> w = new DefaultExcelWriter<T>(
                getRecordDesc());
        w.setWithHeader(withHeader);
        if (writeEditor != null) {
            w.setWriteEditor(writeEditor);
        }
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

    public CsvReader<T> openSheetReader(final HSSFSheet sheet) {
        final DefaultExcelReader<T> r = new DefaultExcelReader<T>(
                getRecordDesc());
        r.setWithHeader(withHeader);
        if (customLayout != null) {
            r.setCustomLayout(customLayout);
        }
        // TODO openで例外時にcloseすること
        r.openSheetReader(sheet);
        return r;
    }

    public CsvWriter<T> openSheetWriter(final HSSFWorkbook workbook,
            final HSSFSheet sheet) {
        final DefaultExcelWriter<T> w = new DefaultExcelWriter<T>(
                getRecordDesc());
        w.setWithHeader(withHeader);
        // TODO openで例外時にcloseすること
        w.openSheetWriter(workbook, sheet);
        return w;
    }

    public void setWriteEditor(final DefaultExcelWriter.WriteEditor writeEditor) {
        this.writeEditor = writeEditor;
    }

}
