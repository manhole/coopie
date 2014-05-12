/*
 * Copyright 2010 manhole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BbbBean;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultExcelWriter.DefaultWriteEditor;
import jp.sourceforge.hotchpotch.coopie.util.FileOperation;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.t2framework.commons.util.ResourceUtil;

public class BeanExcelWriterTest {

    @Test
    public void write_open_null() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<>(AaaBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.build().openWriter(null);
            fail();
        } catch (final NullPointerException npe) {
            assertTrue(npe.getMessage() != null && 0 < npe.getMessage().length());
        }
    }

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<>(AaaBean.class);
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
        final RecordWriter<AaaBean> csvWriter = layout.build().openWriter(baos);

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

    public static void assertWrite2(final ByteArrayOutputStream baos) throws IOException {

        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(1, book.getNumberOfSheets());

        final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(book, book.getSheetAt(0));
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
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<>(AaaBean.class);
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
        final RecordWriter<AaaBean> csvWriter = layout.build().openWriter(baos);

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

    public static void assertWriteNoheader(final ByteArrayOutputStream baos) throws IOException {

        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(1, book.getNumberOfSheets());

        final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(book, book.getSheetAt(0));
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
        final BeanExcelLayout<AaaBean> layout1 = new BeanExcelLayout<>(AaaBean.class);
        layout1.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
            }
        });

        final BeanExcelLayout<BbbBean> layout2 = new BeanExcelLayout<>(BbbBean.class);
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
            final RecordWriter<AaaBean> csvWriter = layout1.openSheetWriter(workbook, sheet);

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
            final RecordWriter<BbbBean> csvWriter = layout2.openSheetWriter(workbook, sheet);

            final BbbBean bean = new BbbBean();
            bean.setAa("a1");
            bean.setBb("b1");
            csvWriter.write(bean);

            csvWriter.close();
        }

        workbook.write(baos);
        baos.close();

        // ## Assert ##
        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(baos.toByteArray()));
        assertEquals(2, book.getNumberOfSheets());

        {
            final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(book,
                    book.getSheet("sheet-a"));
            assertArrayEquals(a("aaa", "ccc", "bbb"), reader.readRecord());
            assertArrayEquals(a("あ1", "う1", "い1"), reader.readRecord());
            assertArrayEquals(a("あ2", "う2", "い2"), reader.readRecord());
            assertNull(reader.readRecord());
            reader.close();
        }
        {
            final DefaultExcelReader.PoiSheetReader reader = new DefaultExcelReader.PoiSheetReader(book,
                    book.getSheet("sheet-b"));
            assertArrayEquals(a("aa", "bb"), reader.readRecord());
            assertArrayEquals(a("a1", "b1"), reader.readRecord());
            assertNull(reader.readRecord());
            reader.close();
        }
    }

    /**
     * 作成したExcelのスタイルを変更できること。(変更しやすいAPIを供えること)
     */
    @Test
    public void write_customStyle() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<>(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("ccc");
                setup.column("aaa");
                setup.column("bbb");
            }
        });
        layout.setWriteEditor(new TestWriteEditor());

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        final RecordWriter<AaaBean> csvWriter = layout.build().openWriter(baos);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc(null);
        csvWriter.write(bean);

        bean.setAaa("あ2");
        bean.setBbb(null);
        bean.setCcc("う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        // ヘッダ行の色が変わっていること
        // nullカラムの色が変わっていること
        final HSSFWorkbook book = new HSSFWorkbook(new ByteArrayInputStream(baos.toByteArray()));
        final File dir = ResourceUtil.getBuildDir(getClass());
        final BufferedOutputStream os = new FileOperation().openBufferedOutputStream(new File(dir, "test.xls"));
        book.write(os);
        os.close();
        assertEquals(1, book.getNumberOfSheets());
        final HSSFSheet sheet = book.getSheetAt(0);

        final HSSFRow firstRow = sheet.getRow(0);
        {
            assertEquals(3, firstRow.getLastCellNum());
            {
                _assertStyle(firstRow.getCell(0));
                _assertStyle(firstRow.getCell(1));
                _assertStyle(firstRow.getCell(2));
            }
        }
    }

    private void _assertStyle(final HSSFCell cell) {
        final HSSFCellStyle style = cell.getCellStyle();
        assertEquals(new HSSFColor.LIGHT_GREEN().getIndex(), style.getFillForegroundColor());
        assertEquals(CellStyle.SOLID_FOREGROUND, style.getFillPattern());
    }

    private static class TestWriteEditor extends DefaultWriteEditor {

        private CellStyle headerStyle;
        private CellStyle errorStyle;

        @Override
        public void begin(final Workbook workbook, final Sheet sheet) {
            super.begin(workbook, sheet);
            headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new HSSFColor.LIGHT_GREEN().getIndex());
            headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);

            errorStyle = workbook.createCellStyle();
            errorStyle.setFillForegroundColor(new HSSFColor.RED().getIndex());
            errorStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        }

        @Override
        public void setCellValue(final Cell cell, final String value) {
            super.setCellValue(cell, value);
            if (null == value) {
                // validate目的
                cell.setCellStyle(errorStyle);
            }
        }

        @Override
        public Cell createCell(final Row row, final short colNum) {
            final Cell cell = super.createCell(row, colNum);
            if (row.getRowNum() == 0) {
                // ヘッダ行にスタイルを適用する
                cell.setCellStyle(headerStyle);
            }
            return cell;
        }

    }

}
