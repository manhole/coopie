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

package jp.sourceforge.hotchpotch.coopie.fl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BigDecimalConverter;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CalendarConverter;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.SkipEmptyLineReadEditor;
import jp.sourceforge.hotchpotch.coopie.csv.MapCsvReaderTest;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.fl.FixedLengthColumnSetup.FixedLengthCompositeColumnSetup;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.CharSequenceWriter;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public class MapFixedLengthReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read_open_null() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();

        // ## Act ##
        // ## Assert ##
        try {
            layout.openReader(null);
            fail();
        } catch (final NullPointerException npe) {
            assertTrue(npe.getMessage() != null
                    && 0 < npe.getMessage().length());
        }
    }

    /**
     * setupしないでopenしようとしたら、エラーにする。
     */
    @Test
    public void read_nosetup() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();

        // ## Act ##
        // ## Assert ##
        boolean success = false;
        try {
            layout.openReader(r);
            success = true;
        } catch (final AssertionError e) {
            logger.debug(e.getMessage());
        }
        if (success) {
            fail();
        }
    }

    /**
     * ファイルヘッダがBeanのプロパティ名と同じ場合。
     * 
     * ※固定長ファイルでは、ヘッダがあっても大事に扱わない。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
                setup.column("bbb", 12, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead1(csvReader, bean);
    }

    /**
     * ファイルヘッダがBeanのプロパティ名と異なる場合。
     * ヘッダに何と書いてあろうとも、指定した文字数順に取り扱うこと。
     * (CSVのときのように、ヘッダの順序に合わせて指定した順を変更しないこと)
     * ※固定長ファイルでは、ヘッダがあっても大事に扱わない。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-2", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
                setup.column("bbb", 12, 30);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead2(csvReader, bean);
    }

    /**
     * ファイルヘッダが無い場合。
     * 
     * ※これが通常の固定長ファイル
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-3", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("ccc", 0, 6);
                setup.column("aaa", 6, 12);
                setup.column("bbb", 12, 20);
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadNoheader(csvReader, bean);
    }

    /**
     * 空白項目がある場合。
     * 
     * CSVでは""はnullとして扱い、" "は" "として扱うが、
     * 固定長では""も" "もnullとする。
     */
    @Test
    public void read3() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-4", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 7);
                setup.column("bbb", 7, 14);
                setup.column("ccc", 14, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead3(csvReader, bean);
    }

    private static void assertRead3(
            final RecordReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        //assertEquals(" ", bean.getCcc());
        assertEquals(null, bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 空ファイルの場合。
     */
    @Test
    public void read_empty() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                // 空ファイルなので、ここは何でも良い
                setup.column("aaa", 0, 7);
                setup.column("bbb", 7, 14);
                setup.column("ccc", 14, 20);
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader(""));

        // ## Assert ##
        assertEquals(false, csvReader.hasNext());

        csvReader.close();
    }

    /**
     * 空行がある場合。
     * 
     * 各要素を"" (null)として扱う。
     */
    @Test
    public void read_empty_row() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-5", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 7);
                setup.column("ccc", 7, 14);
                setup.column("bbb", 14, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadEmptyRow(csvReader, bean);
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read4() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-2", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead4(csvReader, bean);
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
                setup.column("bbb", 12, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        BeanCsvReaderTest.assertReadAfterLast(csvReader, bean);
    }

    /**
     * 指定した長さに満たない行がある場合(空行ではなく)
     * → データがある部分までを読む。足りない部分はnullにする。
     * ※異常データとして扱えた方が良いだろうか。
     */
    @Test
    public void read5() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 3);
                setup.column("bbb", 3, 6);
                setup.column("ccc", 6, 9);
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader("111222333\n44455\n666777888\n"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("111", bean.get("aaa"));
        assertEquals("222", bean.get("bbb"));
        assertEquals("333", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("444", bean.get("aaa"));
        assertEquals("55", bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("666", bean.get("aaa"));
        assertEquals("777", bean.get("bbb"));
        assertEquals("888", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 空行をskipして読めること。
     * 
     */
    @Test
    public void read_skip_emptyline() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-5", "tsv");

        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 7);
                setup.column("ccc", 7, 14);
                setup.column("bbb", 14, 20);
            }
        });
        layout.setWithHeader(true);

        layout.setLineReaderHandler(new SkipEmptyLineReadEditor());

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.get("aaa"));
        assertEquals("い3", bean.get("bbb"));
        assertEquals("う3", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setReaderHandlerではLineReaderHandlerなど何らかのinterfaceをimplしているべき。
     */
    @Test
    public void setup_invalid_readeditor() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();

        // ## Act ##
        // ## Assert ##
        try {
            layout.setReaderHandler(new Object());
            fail();
        } catch (final IllegalArgumentException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * Bean側をBigDecimalで扱えること
     */
    @Test
    public void read_bigDecimal() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<Object> layout = new MapFixedLengthLayout<Object>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 10).withConverter(
                        new BigDecimalConverter());
                setup.column("bbb", 10, 20);
            }
        });
        layout.setWithHeader(true);

        String text;
        {
            final CharSequenceWriter w = new CharSequenceWriter();
            w.writeLine("aaa       bbb       ");
            w.writeLine("11.10     21.02     ");
            w.writeLine("                    ");
            w.writeLine("1,101.45    1,201.56");
            text = w.toString();
        }

        // ## Act ##
        final RecordReader<Map<String, Object>> csvReader = layout
                .openReader(new StringReader(text));

        // ## Assert ##
        final Map<String, Object> bean = CollectionsUtil.newHashMap();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("11.10", ((BigDecimal) bean.get("aaa")).toPlainString());
        assertEquals("21.02", bean.get("bbb"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals(null, bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("1101.45", ((BigDecimal) bean.get("aaa")).toPlainString());
        assertEquals("1,201.56", bean.get("bbb"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * テキスト側が2カラムで、対応するJava側が1プロパティの場合。
     * 年月日と時分秒で列が別れているとする。
     */
    @Test
    public void read_calendar1() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<Object> layout = new MapFixedLengthLayout<Object>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                // ファイルの"ymd"と"hms"列を、JavaBeanの"bbb"プロパティと対応付ける。
                // 2列 <=> 1プロパティ の変換にConverterを使用する。
                // TODO ここでpropertyを呼び忘れた場合のエラーを、わかりやすくする
                setup.columns(
                        new SetupBlock<FixedLengthColumnSetup.FixedLengthCompositeColumnSetup>() {
                            @Override
                            public void setup(
                                    final FixedLengthCompositeColumnSetup compositeSetup) {
                                compositeSetup.column("ymd", 5, 20);
                                compositeSetup.column("hms", 20, 35);
                            }
                        }).toProperty("bbb")
                        .withConverter(new CalendarConverter());
            }
        });
        layout.setWithHeader(true);

        String text;
        {
            final CharSequenceWriter w = new CharSequenceWriter();
            w.writeLine("  aaa            ymd            hms");
            w.writeLine("    a     2011-09-13       17:54:01");
            w.writeLine("    b     2011-01-01       00:00:59");
            text = w.toString();
        }

        // ## Act ##
        final RecordReader<Map<String, Object>> csvReader = layout
                .openReader(new StringReader(text));

        // ## Assert ##
        final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Map<String, Object> bean = CollectionsUtil.newHashMap();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug("{}", bean);
        assertEquals("a", bean.get("aaa"));
        assertEquals("2011/09/13 17:54:01",
                format.format(((Calendar) bean.get("bbb")).getTime()));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("b", bean.get("aaa"));
        assertEquals("2011/01/01 00:00:59",
                format.format(((Calendar) bean.get("bbb")).getTime()));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 複数カラムを1プロパティへ対応づけている時に、一部カラムがnullの場合の挙動
     */
    @Test
    public void read_calendar2() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<Object> layout = new MapFixedLengthLayout<Object>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                // ファイルの"ymd"と"hms"列を、JavaBeanの"bbb"プロパティと対応付ける。
                // 2列 <=> 1プロパティ の変換にConverterを使用する。
                setup.columns(
                        new SetupBlock<FixedLengthColumnSetup.FixedLengthCompositeColumnSetup>() {

                            @Override
                            public void setup(
                                    final FixedLengthCompositeColumnSetup compositeSetup) {
                                compositeSetup.column("ymd", 5, 20);
                                compositeSetup.column("hms", 20, 35);
                            }
                        }).toProperty("bbb")
                        .withConverter(new CalendarConverter());
            }
        });
        layout.setWithHeader(true);

        final String text;
        {
            final CharSequenceWriter w = new CharSequenceWriter();
            w.writeLine("  aaa            ymd            hms");
            w.writeLine("    a     2011-08-13       11:22:33");
            w.writeLine("    b     2011-09-14               ");
            w.writeLine("    c                      12:22:33");
            text = w.toString();
        }

        // ## Act ##
        final RecordReader<Map<String, Object>> csvReader = layout
                .openReader(new StringReader(text));

        // ## Assert ##
        final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final Map<String, Object> bean = CollectionsUtil.newHashMap();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a", bean.get("aaa"));
        assertEquals("2011/08/13 11:22:33",
                format.format(((Calendar) bean.get("bbb")).getTime()));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("b", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("c", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        return BeanFixedLengthReaderTest.getResourceAsReader(suffix, ext);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return BeanFixedLengthReaderTest.getResourceAsStream(suffix, ext);
    }

}
