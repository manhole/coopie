package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BbbBean;

import org.apache.poi.hssf.usermodel.HSSFSheet;
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
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
            }
        });

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        final CsvWriter<AaaBean> csvWriter = layout.openWriter(baos);

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
        assertWrite2(baos);
    }

    public static void assertWrite2(final ByteArrayOutputStream baos)
            throws IOException {

        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(1, book.getNumberOfSheets());

        final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(
                book, book.getSheetAt(0));
        assertArrayEquals(a("aaa", "ccc", "bbb"), reader.readRecord());
        assertArrayEquals(a("あ1", "う1", "い1"), reader.readRecord());
        assertArrayEquals(a("あ2", "う2", "い2"), reader.readRecord());
        assertArrayEquals(a("あ3", "う3", "い3"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void write_noheader() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * プロパティ名, CSV項目名 の順
                 */
                setup.column("ccc");
                setup.column("aaa");
                setup.column("bbb");
            }
        });
        layout.setWithHeader(false);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        final CsvWriter<AaaBean> csvWriter = layout.openWriter(baos);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc("う1");
        csvWriter.write(bean);

        bean.setAaa("あ2");
        bean.setBbb("い2");
        bean.setCcc("う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        assertWriteNoheader(baos);
    }

    public static void assertWriteNoheader(final ByteArrayOutputStream baos)
            throws IOException {

        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(1, book.getNumberOfSheets());

        final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(
                book, book.getSheetAt(0));
        assertArrayEquals(a("う1", "あ1", "い1"), reader.readRecord());
        assertArrayEquals(a("う2", "あ2", "い2"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /**
     * 1シートだけでなく、レイアウトの異なる2シートを1ブックへ出力できること。
     */
    @Test
    public void writeTwoSheets() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout1 = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout1.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
            }
        });

        final BeanExcelLayout<BbbBean> layout2 = new BeanExcelLayout<BbbBean>(
                BbbBean.class);
        layout2.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aa");
                setup.column("bb");
            }
        });

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final HSSFWorkbook workbook = new HSSFWorkbook();

        // ## Act ##
        {
            final HSSFSheet sheet = workbook.createSheet("sheet-a");
            final CsvWriter<AaaBean> csvWriter = layout1.openSheetWriter(sheet);

            final AaaBean bean = new AaaBean();
            bean.setAaa("あ1");
            bean.setBbb("い1");
            bean.setCcc("う1");
            csvWriter.write(bean);

            bean.setAaa("あ2");
            bean.setBbb("い2");
            bean.setCcc("う2");
            csvWriter.write(bean);

            csvWriter.close();
        }
        {
            final HSSFSheet sheet = workbook.createSheet("sheet-b");
            final CsvWriter<BbbBean> csvWriter = layout2.openSheetWriter(sheet);

            final BbbBean bean = new BbbBean();
            bean.setAa("a1");
            bean.setBb("b1");
            csvWriter.write(bean);

            csvWriter.close();
        }

        workbook.write(baos);
        baos.close();

        // ## Assert ##
        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(
                baos.toByteArray()));
        assertEquals(2, book.getNumberOfSheets());

        {
            final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(
                    book, book.getSheet("sheet-a"));
            assertArrayEquals(a("aaa", "ccc", "bbb"), reader.readRecord());
            assertArrayEquals(a("あ1", "う1", "い1"), reader.readRecord());
            assertArrayEquals(a("あ2", "う2", "い2"), reader.readRecord());
            assertNull(reader.readRecord());
            reader.close();
        }
        {
            final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(
                    book, book.getSheet("sheet-b"));
            assertArrayEquals(a("aa", "bb"), reader.readRecord());
            assertArrayEquals(a("a1", "b1"), reader.readRecord());
            assertNull(reader.readRecord());
            reader.close();
        }
    }

}
