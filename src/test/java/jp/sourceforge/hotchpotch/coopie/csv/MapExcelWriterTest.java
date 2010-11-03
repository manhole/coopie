package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.t2framework.commons.util.CollectionsUtil;

public class MapExcelWriterTest {

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##
        final MapExcelLayout layout = new MapExcelLayout();
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
        final CsvWriter<Map<String, String>> csvWriter = layout
                .openWriter(baos);
        System.out.println(csvWriter);

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        bean.put("aaa", "あ1");
        bean.put("bbb", "い1");
        bean.put("ccc", "う1");
        csvWriter.write(bean);

        bean.put("aaa", "あ2");
        bean.put("bbb", "い2");
        bean.put("ccc", "う2");
        csvWriter.write(bean);

        bean.put("aaa", "あ3");
        bean.put("bbb", "い3");
        bean.put("ccc", "う3");
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
