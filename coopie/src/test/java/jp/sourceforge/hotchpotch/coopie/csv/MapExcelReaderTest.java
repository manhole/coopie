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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.TestReadEditor;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ResourceUtil;

public class MapExcelReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read_open_null() throws Throwable {
        // ## Arrange ##
        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        // ## Assert ##
        try {
            layout.openReader(null);
            fail();
        } catch (final NullPointerException npe) {
            assertTrue(npe.getMessage() != null && 0 < npe.getMessage().length());
        }
    }

    @Test
    public void read_open_invalidStream() throws Throwable {
        // ## Arrange ##
        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        // ## Assert ##
        try {
            layout.openReader(new ByteArrayInputStream("invalid_value".getBytes()));
            fail();
        } catch (final IORuntimeException e) {
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

        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead1(csvReader, bean);
    }

    /**
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("あ").toProperty("aaa");
                setup.column("ううう").toProperty("ccc");
                setup.column("いい").toProperty("bbb");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead2(csvReader, bean);
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

        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead3(csvReader, bean);
    }

    /**
     * recordインスタンスをCsvReaderに生成させる。
     */
    @Test
    public void read3_2() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-4", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        MapCsvReaderTest.assertRead3_2(csvReader);
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-3", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * CSVの列順
                 */
                setup.column("ccc");
                setup.column("aaa");
                setup.column("bbb");
            }
        });

        layout.setWithHeader(false);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadNoheader(csvReader, bean);
    }

    @Test
    public void read4() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("あ").toProperty("aaa");
                setup.column("ううう").toProperty("ccc");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead4(csvReader, bean);
    }

    @Test
    public void read5() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-6", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ddd").toProperty("ccc");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead5(csvReader, bean);
    }

    @Test
    public void read_customLayout() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-7", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        final TestReadEditor readEditor = new TestReadEditor();
        layout.setReaderHandler(readEditor);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadCustomLayout(csvReader, bean);
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        BeanCsvReaderTest.assertReadAfterLast(csvReader, bean);
    }

    /**
     * rowが存在するのにlastCellが-1を返すExcelファイルを、エラー無く読めること。
     */
    @Test
    public void read_strange1() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(BeanCsvReaderTest.class.getPackage().getName()
                + "/strange-excel-1", "xls");

        final MapExcelLayout<String> layout = new MapExcelLayout<>();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("この下の行でlastCellNumが-1", bean.get("a"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.get("a"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("ここまで", bean.get("a"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setReaderHandlerではLineReaderHandlerなど何らかのinterfaceをimplしているべき。
     */
    @Test
    public void setup_invalid_readeditor() throws Throwable {
        // ## Arrange ##
        final MapExcelLayout<String> layout = new MapExcelLayout<>();

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
