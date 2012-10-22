package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvWriterTest.AaaBeanBasicSetup;

import org.junit.Test;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ReaderUtil;

public class MapCsvWriterTest {

    @Test
    public void write_open_null() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();

        // ## Act ##
        // ## Assert ##
        try {
            layout.openWriter(null);
            fail();
        } catch (final NullPointerException npe) {
            assertTrue(npe.getMessage() != null
                    && 0 < npe.getMessage().length());
        }
    }

    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
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

        final InputStream is = BeanCsvWriterTest.getResourceAsStream("-1",
                "tsv");
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
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
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

        bean.put("aaa", "あ3");
        bean.put("bbb", "い3");
        bean.put("ccc", "う3");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final Reader r = getResourceAsReader("-1", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダがMapのプロパティ名と異なる場合。
     */
    @Test
    public void write3() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * プロパティ名, CSV項目名 の順
                 */
                setup.column("あ").toProperty("aaa");
                setup.column("ううう").toProperty("ccc");
                setup.column("いい").toProperty("bbb");
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

        final Reader r = getResourceAsReader("-2", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    /**
     * 空白項目がある場合。
     * 
     * ""はnullとして扱い、" "は" "として扱う。
     */
    @Test
    public void write4() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
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

        final Reader r = getResourceAsReader("-4", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void write_noheader() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
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

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
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

        final Reader r = getResourceAsReader("-3", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    @Test
    public void writeCsv() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("ccc");
                setup.column("aaa");
                setup.column("bbb");
            }
        });
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);
        setTo(bean, "a2", "b2", "c2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        BeanCsvWriterTest.assertWriteCsv(lines);
    }

    @Test
    public void write_separator_comma() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        BeanCsvWriterTest.assert_write_separator_comma(lines);
    }

    @Test
    public void write_separator_tab() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.TAB);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        BeanCsvWriterTest.assert_write_separator_tab(lines);
    }

    @Test
    public void write_lineseparator_LF() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setLineSeparator("\n");

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        BeanCsvWriterTest.assert_write_lineseparator_LF(lines);
    }

    @Test
    public void write_quotechar_single() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setQuoteMark('\'');

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        BeanCsvWriterTest.assert_write_quotechar_single(lines);
    }

    @Test
    public void write_quotemode_minimum() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<String> layout = new MapCsvLayout<String>();
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setQuoteMode(QuoteMode.MINIMUM);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(writer);

        final Map<String, String> bean = new TreeMap<String, String>();
        setTo(bean, "a1", "b1", "c\"1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        BeanCsvWriterTest.assert_write_quotemode_minimum(lines);
    }

    private void setTo(final Map<String, String> bean, final String a,
            final String b, final String c) {
        bean.put("aaa", a);
        bean.put("bbb", b);
        bean.put("ccc", c);
    }

    /**
     * Bean側をBigDecimalで扱えること
     */
    @Test
    public void write_bigDecimal() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout<Object> layout = new MapCsvLayout<Object>();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa").withConverter(new BigDecimalConverter());
                setup.column("bbb");
            }
        });

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
            final ElementReader reader = new CsvElementInOut(
                    new DefaultCsvSetting())
                    .openReader(new StringReader(lines));
            assertArrayEquals(a("aaa", "bbb"), reader.readRecord());
            assertArrayEquals(a("11.10", "21.02"), reader.readRecord());
            assertArrayEquals(a("", ""), reader.readRecord());
            assertArrayEquals(a("1,101.45", "1,201.56"), reader.readRecord());
            assertNull(reader.readRecord());
            reader.close();
        }
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        final Reader reader = BeanCsvReaderTest
                .getResourceAsReader(suffix, ext);
        return reader;
    }

}
