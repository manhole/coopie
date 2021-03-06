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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BbbBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.TestReadEditor;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.slf4j.Logger;

public class BeanExcelReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read_open_null() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.build().openReader(null);
            fail();
        } catch (final NullPointerException npe) {
            assertTrue(npe.getMessage() != null && 0 < npe.getMessage().length());
        }
    }

    @Test
    public void read_open_invalidStream() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.build().openReader(new ByteArrayInputStream("invalid_value".getBytes()));
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * ヘッダがBeanのプロパティ名と同じ場合。
     *
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead1(csvReader, bean);
    }

    /**
     * ヘッダがBeanのプロパティ名と異なる場合。
     * ヘッダ名とbeanのプロパティ名をマッピングすること。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("いい").toProperty("bbb");
                setup.column("あ").toProperty("aaa");
                setup.column("ううう").toProperty("ccc");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead2(csvReader, bean);
    }

    /**
     * ヘッダがBeanのプロパティ名と異なる場合。
     * ヘッダ名とbeanのプロパティ名をマッピングすること。
     *
     * ヘッダ名が事前に決まらない(ロケールにより決定するなどで何パターンか有り得る)場合。
     */
    @Test
    public void read2_2() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                {
                    final DefaultCsvColumnDef def = new DefaultCsvColumnDef();
                    def.setPropertyName("bbb");
                    def.setColumnNameMatcher(new ContainsNameMatcher("い"));
                    setup.column(def);
                }
                {
                    final DefaultCsvColumnDef def = new DefaultCsvColumnDef();
                    def.setPropertyName("aaa");
                    def.setColumnNameMatcher(new ContainsNameMatcher("あ"));
                    setup.column(def);
                }
                {
                    final DefaultCsvColumnDef def = new DefaultCsvColumnDef();
                    def.setPropertyName("ccc");
                    def.setColumnNameMatcher(new ContainsNameMatcher("う"));
                    setup.column(def);
                }
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead2(csvReader, bean);
    }

    /**
     * 空白項目がある場合。
     *
     * ""はnullとして扱い、" "は" "として扱う。
     */
    @Test
    public void read3() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-4", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead3(csvReader, bean);
    }

    /**
     * ヘッダが無い場合。
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-3", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * CSVの列順
                 */
                setup.column("ううう").toProperty("ccc");
                setup.column("あ").toProperty("aaa");
                setup.column("いい").toProperty("bbb");
            }
        });

        layout.setWithHeader(false);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadNoheader(csvReader, bean);
    }

    /**
     * 空行がある場合。
     *
     * 各要素を"" (null)として扱う。
     */
    @Test
    public void read_empty_row() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-5", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadEmptyRow(csvReader, bean);
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read4() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("あ").toProperty("aaa");
                setup.column("ううう").toProperty("ccc");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead4(csvReader, bean);
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read5() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-6", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ddd").toProperty("ccc");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead5(csvReader, bean);
    }

    @Test
    public void read_customLayout() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-7", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);
        final TestReadEditor readEditor = new TestReadEditor();
        layout.setReaderHandler(readEditor);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadCustomLayout(csvReader, bean);
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.build().openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadAfterLast(csvReader, bean);
    }

    /**
     * 1シートだけでなく、レイアウトの異なる2シートを持つ1ブックから入力できること。
     */
    @Test
    public void readTwoSheets() throws Throwable {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        {
            final HSSFWorkbook workbook = new HSSFWorkbook();

            {
                final DefaultExcelWriter.PoiSheetWriter writer = new DefaultExcelWriter.PoiSheetWriter(workbook,
                        workbook.createSheet("S1"));
                writer.open();
                writer.writeRecord(a("aaa", "bbb", "ccc"));
                writer.writeRecord(a("a1", "b1", "c1"));
                writer.close();
            }
            {
                final DefaultExcelWriter.PoiSheetWriter writer = new DefaultExcelWriter.PoiSheetWriter(workbook,
                        workbook.createSheet("S2"));
                writer.open();
                writer.writeRecord(a("aa", "bb"));
                writer.writeRecord(a("aa1", "bb1"));
                writer.writeRecord(a("aa2", "bb2"));
                writer.close();
            }

            workbook.write(baos);
        }

        final BeanExcelLayout<AaaBean> layout1 = new BeanExcelLayout<AaaBean>(AaaBean.class);
        final BeanExcelLayout<BbbBean> layout2 = new BeanExcelLayout<BbbBean>(BbbBean.class);

        final HSSFWorkbook workbook = new HSSFWorkbook(new ByteArrayInputStream(baos.toByteArray()));

        // ## Act ##
        // ## Assert ##
        {
            final HSSFSheet sheet = workbook.getSheet("S1");
            final RecordReader<AaaBean> csvReader = layout1.openSheetReader(sheet);

            final AaaBean bean = new AaaBean();
            assertEquals(true, csvReader.hasNext());

            csvReader.read(bean);
            assertEquals("a1", bean.getAaa());
            assertEquals("b1", bean.getBbb());
            assertEquals("c1", bean.getCcc());

            assertEquals(false, csvReader.hasNext());
            csvReader.close();
        }
        {
            final HSSFSheet sheet = workbook.getSheet("S2");
            final RecordReader<BbbBean> csvReader = layout2.openSheetReader(sheet);

            final BbbBean bean = new BbbBean();
            assertEquals(true, csvReader.hasNext());
            csvReader.read(bean);
            assertEquals("aa1", bean.getAa());
            assertEquals("bb1", bean.getBb());

            assertEquals(true, csvReader.hasNext());
            csvReader.read(bean);
            assertEquals("aa2", bean.getAa());
            assertEquals("bb2", bean.getBb());

            assertEquals(false, csvReader.hasNext());
            csvReader.close();
        }
    }

    /**
     * setReaderHandlerではLineReaderHandlerなど何らかのinterfaceをimplしているべき。
     */
    @Test
    public void setup_invalid_readeditor() throws Throwable {
        // ## Arrange ##
        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(AaaBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.setReaderHandler(new Object());
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }
    }

}
