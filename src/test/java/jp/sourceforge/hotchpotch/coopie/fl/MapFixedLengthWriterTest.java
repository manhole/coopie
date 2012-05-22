package jp.sourceforge.hotchpotch.coopie.fl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BigDecimalConverter;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ReaderUtil;
import org.t2framework.commons.util.ResourceUtil;

public class MapFixedLengthWriterTest {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * setupしないでopenしようとしたら、エラーにする。
     */
    @Test
    public void read_nosetup() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();

        // ## Act ##
        // ## Assert ##
        try {
            layout.openWriter(new StringWriter());
            fail();
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * 出力できること。
     * データは右側に寄っていること。
     */
    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<String> layout = new MapFixedLengthLayout<String>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("bbb", 5, 12);
                setup.column("ccc", 12, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        bean.put("aaa", "あ1");
        bean.put("bbb", "い1");
        bean.put("ccc", "う1");
        csvWriter.write(bean);

        bean.put("aaa", "あ2");
        bean.put("bbb", "い2");
        bean.put("ccc", "う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanFixedLengthWriterTest.class.getName() + "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
        assertEquals(expected, actual);
    }

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##
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
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

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
        final String actual = writer.toString();

        final String expected = ReaderUtil.readText(getResourceAsReader("-1",
                "tsv"));
        assertEquals(expected, actual);
    }

    /**
     * 空白項目がある場合。
     * 
     * CSVでは""はnullとして扱い、" "は" "として扱うが、
     * 固定長では""も" "もnullとし、スペースで埋める。
     */
    @Test
    public void write4() throws Throwable {
        // ## Arrange ##
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
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        bean.put("aaa", "あ1");
        bean.put("bbb", "い1");
        bean.put("ccc", " ");
        csvWriter.write(bean);

        bean.put("aaa", null);
        bean.put("bbb", "い2");
        bean.put("ccc", null);
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final String expected = ReaderUtil.readText(getResourceAsReader("-4-2",
                "tsv"));
        assertEquals(expected, actual);
    }

    /**
     * ファイルヘッダが無い場合。
     * 
     * ※これが通常の固定長ファイル
     */
    @Test
    public void write_noheader() throws Throwable {
        // ## Arrange ##
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
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        bean.put("aaa", "あ1");
        bean.put("bbb", "い1");
        bean.put("ccc", "う1");
        csvWriter.write(bean);

        bean.put("aaa", "あ2");
        bean.put("bbb", "い2");
        bean.put("ccc", "う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final String expected = ReaderUtil.readText(getResourceAsReader("-3",
                "tsv"));
        assertEquals(expected, actual);
    }

    /*
     * TODO 定義した長さよりも実際のデータが長い場合
     */

    /**
     * Bean側をBigDecimalで扱えること
     */
    @Test
    public void write_bigDecimal() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout<Object> layout = new MapFixedLengthLayout<Object>();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 10).converter(new BigDecimalConverter());
                setup.column("bbb", 10, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, Object>> csvWriter = layout
                .openWriter(writer);

        final Map<String, Object> bean = new TreeMap<String, Object>();
        bean.put("aaa", new BigDecimal("11.1"));
        bean.put("bbb", "21.02");
        csvWriter.write(bean);

        bean.clear();
        csvWriter.write(bean);

        bean.put("aaa", new BigDecimal("1101.45"));
        bean.put("bbb", "1,201.56");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();

        {
            final LineReadable reader = new LineReadable(
                    new StringReader(lines));
            assertEquals("       aaa       bbb", reader.readLineBody());
            assertEquals("     11.10     21.02", reader.readLineBody());
            assertEquals("                    ", reader.readLineBody());
            assertEquals("  1,101.45  1,201.56", reader.readLineBody());
            assertNull(reader.readLineBody());
            reader.close();
        }
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        return BeanFixedLengthReaderTest.getResourceAsReader(suffix, ext);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanFixedLengthWriterTest.class.getName() + suffix, ext);
    }

}
