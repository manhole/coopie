package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ReaderUtil;

public class MapCsvWriterTest {

    @Test
    public void write_open_null() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();

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
        final MapCsvLayout layout = new MapCsvLayout();

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
        final MapCsvLayout layout = new MapCsvLayout();
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

        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1",
                "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダがMapのプロパティ名と異なる場合。
     */
    @Test
    public void write3() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * プロパティ名, CSV項目名 の順
                 */
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
                setup.column("bbb", "いい");
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

        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2",
                "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
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
        final MapCsvLayout layout = new MapCsvLayout();

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

        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-4",
                "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void write_noheader() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
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

        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-3",
                "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
        assertEquals(expected, actual);
    }

    @Test
    public void writeCsv() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
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

    private void setTo(final Map<String, String> bean, final String a,
            final String b, final String c) {
        bean.put("aaa", a);
        bean.put("bbb", b);
        bean.put("ccc", c);
    }

}
