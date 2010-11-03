package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

public class BeanExcelWriterTest {

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
            }
        });

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        final CsvWriter<AaaBean> csvWriter = layout.openWriter(baos);
        System.out.println(csvWriter);

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
        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(1, book.getNumberOfSheets());

        {
            final BeanExcelLayout<AaaBean> layout2 = new BeanExcelLayout<AaaBean>(
                    AaaBean.class);

            final CsvReader<AaaBean> csvReader = layout2
                    .openReader(new ByteArrayInputStream(baos.toByteArray()));
            {
                assertEquals(true, csvReader.hasNext());
                final AaaBean record = csvReader.read();
                assertEquals("あ1", record.getAaa());
                assertEquals("い1", record.getBbb());
                assertEquals("う1", record.getCcc());
            }
            {
                assertEquals(true, csvReader.hasNext());
                final AaaBean record = csvReader.read();
                assertEquals("あ2", record.getAaa());
                assertEquals("い2", record.getBbb());
                assertEquals("う2", record.getCcc());
            }
            {
                assertEquals(true, csvReader.hasNext());
                final AaaBean record = csvReader.read();
                assertEquals("あ3", record.getAaa());
                assertEquals("い3", record.getBbb());
                assertEquals("う3", record.getCcc());
            }

            assertEquals(false, csvReader.hasNext());

            csvReader.close();
        }

    }

}
