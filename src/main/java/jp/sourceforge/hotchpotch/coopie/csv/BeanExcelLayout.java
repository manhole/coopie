package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class BeanExcelLayout<BEAN> extends AbstractBeanCsvLayout<BEAN>
        implements ExcelInOut<BEAN> {

    private DefaultExcelWriter.WriteEditor writeEditor_;

    public BeanExcelLayout(final Class<BEAN> beanClass) {
        super(beanClass);
    }

    @Override
    public RecordReader<BEAN> openReader(final InputStream is) {
        if (is == null) {
            throw new NullPointerException("is");
        }

        prepareOpen();
        final DefaultExcelReader<BEAN> r = new DefaultExcelReader<BEAN>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementReaderHandler(getElementReaderHandler());

        // TODO openで例外時にcloseすること
        r.open(is);
        return r;
    }

    @Override
    public RecordWriter<BEAN> openWriter(final OutputStream os) {
        if (os == null) {
            throw new NullPointerException("os");
        }

        prepareOpen();
        final DefaultExcelWriter<BEAN> w = new DefaultExcelWriter<BEAN>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        if (writeEditor_ != null) {
            w.setWriteEditor(writeEditor_);
        }
        // TODO openで例外時にcloseすること
        w.open(os);
        return w;
    }

    public RecordReader<BEAN> openSheetReader(final HSSFSheet sheet) {
        prepareOpen();
        final DefaultExcelReader<BEAN> r = new DefaultExcelReader<BEAN>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementReaderHandler(getElementReaderHandler());

        // TODO openで例外時にcloseすること
        r.openSheetReader(sheet);
        return r;
    }

    public RecordWriter<BEAN> openSheetWriter(final HSSFWorkbook workbook,
            final HSSFSheet sheet) {
        prepareOpen();
        final DefaultExcelWriter<BEAN> w = new DefaultExcelWriter<BEAN>(
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
