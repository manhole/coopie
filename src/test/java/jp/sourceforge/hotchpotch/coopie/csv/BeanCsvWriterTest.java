package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CccBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.DddBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.EeeBean;
import jp.sourceforge.hotchpotch.coopie.util.Text;

import org.junit.Test;
import org.t2framework.commons.util.ReaderUtil;
import org.t2framework.commons.util.ResourceUtil;

public class BeanCsvWriterTest {

    @Test
    public void write_open_null() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

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
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

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
        final String actual = writer.toString();

        final InputStream is = getResourceAsStream("-1", "tsv");
        final InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        //        final String expected = ReaderUtil.readText(reader);
        //        assertEquals(expected, actual);
        new CsvAssert().assertCsvEquals(reader, new StringReader(actual));
    }

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
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
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

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
        final String actual = writer.toString();

        final Reader r = getResourceAsReader("-1", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void write3() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
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
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

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
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc(" ");
        csvWriter.write(bean);

        bean.setAaa(null);
        bean.setBbb("い2");
        bean.setCcc(null);
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final Reader r = getResourceAsReader("-4", "tsv");
        //        final InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        //        final String expected = ReaderUtil.readText(reader);
        //        assertEquals(expected, actual);
        new CsvAssert().assertCsvEquals(r, new StringReader(actual));
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void write_noheader() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
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
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

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
        final String actual = writer.toString();

        final Reader r = getResourceAsReader("-3", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    @Test
    public void writeCsv() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
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
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

        final AaaBean bean = new AaaBean();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);
        setTo(bean, "a2", "b2", "c2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        assertWriteCsv(lines);
    }

    static void assertWriteCsv(final String lines) {
        final Text text = new Text(lines);
        assertEquals(3, text.getLineSize());
        {
            final Text line = text.getLine(0);
            assertEquals("ccc,aaa,bbb", line.deleteChar('\"').toString());
        }
        {
            final Text line = text.getLine(1);
            assertEquals("c1,a1,b1", line.deleteChar('\"').toString());
        }
        {
            final Text line = text.getLine(2);
            assertEquals("c2,a2,b2", line.deleteChar('\"').toString());
        }
    }

    static class AaaBeanBasicSetup implements SetupBlock<CsvColumnSetup> {
        @Override
        public void setup(final CsvColumnSetup setup) {
            setup.column("aaa", "a");
            setup.column("bbb", "b");
            setup.column("ccc", "c");
        }
    }

    static String CRLF = CsvSetting.CRLF;
    static String LF = CsvSetting.LF_S;

    @Test
    public void write_separator_comma() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

        final AaaBean bean = new AaaBean();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        assert_write_separator_comma(lines);
    }

    static final String V_WRITE_SEPARATOR_COMMA = "\"a\",\"b\",\"c\"" + CRLF
            + "\"a1\",\"b1\",\"c1\"" + CRLF;

    static void assert_write_separator_comma(final String lines) {
        assertEquals(V_WRITE_SEPARATOR_COMMA, lines);
    }

    @Test
    public void write_separator_tab() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.TAB);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

        final AaaBean bean = new AaaBean();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        assert_write_separator_tab(lines);
    }

    static final String V_WRITE_SEPARATOR_TAB = "\"a\"\t\"b\"\t\"c\"" + CRLF
            + "\"a1\"\t\"b1\"\t\"c1\"" + CRLF;

    static void assert_write_separator_tab(final String lines) {
        assertEquals(V_WRITE_SEPARATOR_TAB, lines);
    }

    @Test
    public void write_lineseparator_LF() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setLineSeparator("\n");

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

        final AaaBean bean = new AaaBean();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        assert_write_lineseparator_LF(lines);
    }

    static final String V_WRITE_LINESEPARATOR_LF = "\"a\",\"b\",\"c\"" + LF
            + "\"a1\",\"b1\",\"c1\"" + LF;

    static void assert_write_lineseparator_LF(final String lines) {
        assertEquals(V_WRITE_LINESEPARATOR_LF, lines);
    }

    @Test
    public void write_quotechar_single() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setQuoteMark('\'');

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<AaaBean> csvWriter = layout.openWriter(writer);

        final AaaBean bean = new AaaBean();
        setTo(bean, "a1", "b1", "c1");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();
        assert_write_quotechar_single(lines);
    }

    static final String V_WRITE_QUOTECHAR_SINGLE = "'a','b','c'" + CRLF
            + "'a1','b1','c1'" + CRLF;

    static void assert_write_quotechar_single(final String lines) {
        assertEquals(V_WRITE_QUOTECHAR_SINGLE, lines);
    }

    private void setTo(final AaaBean bean, final String a, final String b,
            final String c) {
        bean.setAaa(a);
        bean.setBbb(b);
        bean.setCcc(c);
    }

    /**
     * 列名のみをアノテーションで指定して出力できること。
     * (列順は不定)
     */
    @Test
    public void write_annotation_1() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<CccBean> layout = BeanCsvLayout
                .getInstance(CccBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<CccBean> csvWriter = layout.openWriter(writer);

        final CccBean bean = new CccBean();
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
        final String actual = writer.toString();

        new CsvAssert().assertCsvEquals(getResourceAsReader("-2", "tsv"),
                new StringReader(actual));
    }

    /**
     * 列名と列順をアノテーションで指定して出力できること。
     */
    @Test
    public void write_annotation_2() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<EeeBean> layout = BeanCsvLayout
                .getInstance(EeeBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<EeeBean> csvWriter = layout.openWriter(writer);

        final EeeBean bean = new EeeBean();
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
        final String actual = writer.toString();

        final Reader r = getResourceAsReader("-2", "tsv");
        final String expected = ReaderUtil.readText(r);
        assertEquals(expected, actual);
    }

    /**
     * 列順序のみをアノテーションで指定して出力できること。
     * (列名は、property名)
     */
    @Test
    public void write_annotation_3() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<DddBean> layout = BeanCsvLayout
                .getInstance(DddBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<DddBean> csvWriter = layout.openWriter(writer);

        final DddBean bean = new DddBean();
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
        final String actual = writer.toString();

        final InputStream is = getResourceAsStream("-1", "tsv");
        final InputStreamReader reader = new InputStreamReader(is, "UTF-8");
        final String expected = ReaderUtil.readText(reader);
        assertEquals(expected, actual);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanCsvWriterTest.class.getName() + suffix, ext);
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        final Reader reader = BeanCsvReaderTest
                .getResourceAsReader(suffix, ext);
        return reader;
    }

}
