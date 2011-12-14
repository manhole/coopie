package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class BeanExcelLayout<T> extends AbstractBeanCsvLayout<T> implements
        ExcelInOut<T> {

    private DefaultExcelWriter.WriteEditor writeEditor_;

    public BeanExcelLayout(final Class<T> beanClass) {
        super(beanClass);
    }

    @Override
    public RecordReader<T> openReader(final InputStream is) {
        final DefaultExcelReader<T> r = new DefaultExcelReader<T>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementReaderHandler(getElementReaderHandler());

        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public RecordWriter<T> openWriter(final OutputStream os) {
        final DefaultExcelWriter<T> w = new DefaultExcelWriter<T>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        if (writeEditor_ != null) {
            w.setWriteEditor(writeEditor_);
        }
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

    public RecordReader<T> openSheetReader(final HSSFSheet sheet) {
        final DefaultExcelReader<T> r = new DefaultExcelReader<T>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementReaderHandler(getElementReaderHandler());

        // TODO openで例外時にcloseすること
        r.openSheetReader(sheet);
        return r;
    }

    public RecordWriter<T> openSheetWriter(final HSSFWorkbook workbook,
            final HSSFSheet sheet) {
        final DefaultExcelWriter<T> w = new DefaultExcelWriter<T>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        // TODO openで例外時にcloseすること
        w.openSheetWriter(workbook, sheet);
        return w;
    }

    public void setWriteEditor(final DefaultExcelWriter.WriteEditor writeEditor) {
        writeEditor_ = writeEditor;
    }

}
