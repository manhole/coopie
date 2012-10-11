package jp.sourceforge.hotchpotch.coopie.fl;

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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BigDecimalBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BigDecimalConverter;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CalendarBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CalendarConverter;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.fl.BeanFixedLengthReaderTest.FlAaaBean;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.ReaderUtil;
import org.t2framework.commons.util.ResourceUtil;

public class BeanFixedLengthWriterTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void write_open_null() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);

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

    /**
     * setupしないでopenしようとしたら、エラーにする。
     */
    @Test
    public void write_nosetup() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        // ## Assert ##
        boolean success = false;
        try {
            layout.openWriter(new StringWriter());
            success = true;
        } catch (final AssertionError e) {
            logger.debug(e.getMessage());
        }
        if (success) {
            fail();
        }
    }

    /**
     * 出力できること。
     * データは右側に寄っていること。
     */
    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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

        final String expected = ReaderUtil.readText(getResourceAsReader("-3",
                "tsv"));
        assertEquals(expected, actual);
    }

    /*
     * TODO 定義した長さよりも実際のデータが長い場合
     */

    /**
     * アノテーションから列サイズを取得できること。
     */
    @Test
    public void write_annotation_1() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<FlAaaBean> layout = BeanFixedLengthLayout
                .getInstance(FlAaaBean.class);

        final StringWriter writer = new StringWriter();

        // ## Act ##
        final RecordWriter<FlAaaBean> csvWriter = layout.openWriter(writer);

        // ## Act ##
        final FlAaaBean bean = new FlAaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc("う");
        csvWriter.write(bean);

        // ## Assert ##
        final String actual = writer.toString();
        assertEquals(" あ1い1う\r\n", actual);
        csvWriter.close();
    }

    /**
     * 列の文字数がサロゲートペアに対応していること。
     */
    @Test
    public void write_annotation_2() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<FlAaaBean> layout = BeanFixedLengthLayout
                .getInstance(FlAaaBean.class);

        final StringWriter writer = new StringWriter();

        // ## Act ##
        final RecordWriter<FlAaaBean> csvWriter = layout.openWriter(writer);

        // ## Act ##
        final FlAaaBean bean = new FlAaaBean();
        bean.setAaa("𠮷野");
        bean.setBbb(" き");
        bean.setCcc("家");
        csvWriter.write(bean);

        // ## Assert ##
        final String actual = writer.toString();
        assertEquals(" 𠮷野 き家\r\n", actual);
        csvWriter.close();
    }

    /**
     * Bean側をBigDecimalで扱えること
     */
    @Test
    public void write_bigDecimal() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<BigDecimalBean> layout = new BeanFixedLengthLayout<BigDecimalBean>(
                BigDecimalBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 10).withConverter(
                        new BigDecimalConverter());
                setup.column("bbb", 10, 20);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<BigDecimalBean> csvWriter = layout
                .openWriter(writer);

        final BigDecimalBean bean = new BigDecimalBean();
        bean.setAaa(new BigDecimal("11.1"));
        bean.setBbb("21.02");
        csvWriter.write(bean);

        bean.setAaa(null);
        bean.setBbb(null);
        csvWriter.write(bean);

        bean.setAaa(new BigDecimal("1101.45"));
        bean.setBbb("1,201.56");
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

    /**
     * テキスト側が2カラムで、対応するJava側が1プロパティの場合。
     * 年月日と時分秒で列が別れているとする。
     */
    @Test
    public void write_calendar() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<CalendarBean> layout = new BeanFixedLengthLayout<CalendarBean>(
                CalendarBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                // ファイルの"ymd"と"hms"列を、JavaBeanの"bbb"プロパティと対応付ける。
                // 2列 <=> 1プロパティ の変換にConverterを使用する。
                setup.columns(setup.c("ymd", 5, 20), setup.c("hms", 20, 35))
                        .toProperty("bbb")
                        .withConverter(new CalendarConverter());
            }
        });
        layout.setWithHeader(true);

        final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final RecordWriter<CalendarBean> csvWriter = layout.openWriter(writer);

        final CalendarBean bean = new CalendarBean();
        bean.setAaa("a");
        {
            final Calendar c = Calendar.getInstance();
            c.setTime(format.parse("2011/09/13 17:54:01"));
            bean.setBbb(c);
        }
        csvWriter.write(bean);

        bean.setAaa("b");
        {
            final Calendar c = Calendar.getInstance();
            c.setTime(format.parse("2011/01/01 00:00:59"));
            bean.setBbb(c);
        }
        csvWriter.write(bean);

        bean.setAaa("c");
        bean.setBbb(null);
        csvWriter.write(bean);
        csvWriter.close();

        // ## Assert ##
        final String lines = writer.toString();

        final LineReadable reader = new LineReadable(new StringReader(lines));
        assertEquals("  aaa            ymd            hms",
                reader.readLineBody());
        assertEquals("    a     2011-09-13       17:54:01",
                reader.readLineBody());
        assertEquals("    b     2011-01-01       00:00:59",
                reader.readLineBody());
        assertEquals("    c                              ",
                reader.readLineBody());
        assertNull(reader.readLineBody());
        reader.close();
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        return BeanFixedLengthReaderTest.getResourceAsReader(suffix, ext);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanFixedLengthWriterTest.class.getName() + suffix, ext);
    }

}
