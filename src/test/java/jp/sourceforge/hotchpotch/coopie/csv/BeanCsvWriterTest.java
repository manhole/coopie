package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CsvSetting;

import org.junit.Test;
import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.ReaderUtil;
import org.t2framework.commons.util.ResourceUtil;

import au.com.bytecode.opencsv.CSVWriter;

public class BeanCsvWriterTest {

    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final BeanCsvWriter<AaaBean> csvWriter = new BeanCsvWriter<AaaBean>(
            AaaBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        csvWriter.open(writer);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc("う1");
        csvWriter.write(bean);

        bean.setAaa("あ2");
        bean.setBbb("い2");
        bean.setCcc("う2");
        csvWriter.write(bean);

        bean.setAaa("あ3");
        bean.setBbb("い3");
        bean.setCcc("う3");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvWriterTest.class.getName() + "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
            "UTF-8"));
        assertEquals(expected, actual);
    }

    public static class BeanCsvWriter<T> implements Closable {

        private CsvSetting csvSetting = new CsvSetting();

        public CsvSetting getCsvSetting() {
            return csvSetting;
        }

        public void setCsvSetting(final CsvSetting csvSetting) {
            this.csvSetting = csvSetting;
        }

        private final BeanDesc<T> beanDesc;
        protected boolean closed = true;
        /**
         * BeanCsvWriter close時に、Writerを一緒にcloseする場合はtrue。
         */
        private boolean closeWriter = true;
        private CSVWriter csvWriter;
        private final PropertyDesc<T>[] propertyDescs;

        public void setCloseWriter(final boolean closeWriter) {
            this.closeWriter = closeWriter;
        }

        @SuppressWarnings("unchecked")
        public BeanCsvWriter(final Class<T> beanClass) {
            beanDesc = BeanDescFactory.getBeanDesc(beanClass);
            final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
            propertyDescs = pds.toArray(new PropertyDesc[pds.size()]);
        }

        public void open(final Writer writer) {
            csvWriter = new CSVWriter(writer, csvSetting.getElementSeparator(),
                csvSetting.getQuoteMark(), csvSetting.getLineSeparator());
            writeHeader();
            closed = false;
        }

        private void writeHeader() {
            final String[] line = new String[propertyDescs.length];
            int i = 0;
            for (final PropertyDesc<T> pd : propertyDescs) {
                final String v = pd.getPropertyName();
                line[i] = v;
                i++;
            }

            csvWriter.writeNext(line);

        }

        public void write(final T bean) {
            final String[] line = new String[propertyDescs.length];
            int i = 0;
            for (final PropertyDesc<T> pd : propertyDescs) {
                final Object v = pd.getValue(bean);
                line[i] = v != null ? String.valueOf(v) : null;
                i++;
            }
            csvWriter.writeNext(line);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (closeWriter) {
                csvWriter.close();
                csvWriter = null;
            }
        }

    }

}
