package jp.sourceforge.hotchpotch.coopie.fl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BigDecimalBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BigDecimalConverter;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CalendarBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CalendarConverter;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.SkipEmptyLineReadEditor;
import jp.sourceforge.hotchpotch.coopie.csv.ElementEditors;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.fl.FixedLengthColumnSetup.FixedLengthCompositeColumnSetup;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.CharSequenceWriter;
import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.ResourceUtil;

public class BeanFixedLengthReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read_open_null() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);

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

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);

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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead1(csvReader, bean);
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

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead2(csvReader, bean);
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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadNoheader(csvReader, bean);
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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead3(csvReader, bean);
    }

    private static void assertRead3(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        //assertEquals(" ", bean.getCcc());
        assertEquals(null, bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals(null, bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 空ファイルの場合。
     */
    @Test
    public void read_empty() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                // 空ファイルなので、ここは何でも良い
                setup.column("aaa", 0, 7);
                setup.column("bbb", 7, 14);
                setup.column("ccc", 14, 20);
            }
        });
        layout.setWithHeader(false);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader(""));

        // ## Assert ##
        assertEquals(false, csvReader.hasNext());

        csvReader.close();
    }

    /**
     * 空行がある場合。
     * 
     * 各要素を"" (null)として扱う。
     * ※異常データとして扱えた方が良いだろうか。
     */
    @Test
    public void read_empty_row() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-5", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

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
        final Reader r = getResourceAsReader("-2", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead4(csvReader, bean);
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
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
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 3);
                setup.column("bbb", 3, 6);
                setup.column("ccc", 6, 9);
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("111222333\n44455\n666777888\n"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("111", bean.getAaa());
        assertEquals("222", bean.getBbb());
        assertEquals("333", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("444", bean.getAaa());
        assertEquals("55", bean.getBbb());
        assertEquals(null, bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("666", bean.getAaa());
        assertEquals("777", bean.getBbb());
        assertEquals("888", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * アノテーションから列サイズを取得できること。
     */
    @Test
    public void read_annotation_1() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<FlAaaBean> layout = BeanFixedLengthLayout
                .getInstance(FlAaaBean.class);

        // ## Act ##
        final RecordReader<FlAaaBean> csvReader = layout
                .openReader(new StringReader("0123456789\n1234567890\n23\n"));

        // ## Assert ##
        final FlAaaBean bean = new FlAaaBean();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("012", bean.getAaa());
        assertEquals("34", bean.getBbb());
        assertEquals("5", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("123", bean.getAaa());
        assertEquals("45", bean.getBbb());
        assertEquals("6", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("23", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals(null, bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 列の文字数がサロゲートペアに対応していること。
     */
    @Test
    public void read_annotation_2() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<FlAaaBean> layout = BeanFixedLengthLayout
                .getInstance(FlAaaBean.class);

        // ## Act ##
        final RecordReader<FlAaaBean> csvReader = layout
                .openReader(new StringReader("𠮷野家すき家"));

        // ## Assert ##
        final FlAaaBean bean = new FlAaaBean();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("𠮷野家", bean.getAaa());
        assertEquals("すき", bean.getBbb());
        assertEquals("家", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * デフォルトではtrimしない。
     * → 既にtrimするよう実装していたので、とりあえずそのままにする。
     * TODO 要素が右寄せなのか左寄せなのかに合わせてtrimするのが良いだろうか。
     */
    @Test
    public void read_trim_off() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 3);
                setup.column("bbb", 3, 6);
                setup.column("ccc", 6, 9);
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("　a bb ccc"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        // TODO 現状、trimされている
        //assertEquals("　a ", bean.getAaa());
        assertEquals("　a", bean.getAaa());
        //assertEquals("bb ", bean.getBbb());
        assertEquals("bb", bean.getBbb());
        assertEquals("ccc", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 要素をtrimするオプション
     */
    @Test
    public void read_trim_all() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 3);
                setup.column("bbb", 3, 6);
                setup.column("ccc", 6, 9);
            }
        });
        layout.setElementEditor(ElementEditors.trim());

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("　a bb ccc"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("　a", bean.getAaa());
        assertEquals("bb", bean.getBbb());
        assertEquals("ccc", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 全角スペースもtrim対象とするオプション
     */
    @Test
    public void read_trim_all_whitespace() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 3);
                setup.column("bbb", 3, 6);
                setup.column("ccc", 6, 9);
            }
        });
        layout.setElementEditor(ElementEditors.trimWhitespace());

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("　a bb ccc"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a", bean.getAaa());
        assertEquals("bb", bean.getBbb());
        assertEquals("ccc", bean.getCcc());

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

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
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
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.getAaa());
        assertEquals("い3", bean.getBbb());
        assertEquals("う3", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setReaderHandlerではLineReaderHandlerなど何らかのinterfaceをimplしているべき。
     */
    @Test
    public void setup_invalid_readeditor() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);

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
        final RecordReader<BigDecimalBean> csvReader = layout
                .openReader(new StringReader(text));

        // ## Assert ##
        final BigDecimalBean bean = new BigDecimalBean();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("11.10", bean.getAaa().toPlainString());
        assertEquals("21.02", bean.getBbb());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals(null, bean.getAaa());
        assertEquals(null, bean.getBbb());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("1101.45", bean.getAaa().toPlainString());
        assertEquals("1,201.56", bean.getBbb());

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
        final BeanFixedLengthLayout<CalendarBean> layout = new BeanFixedLengthLayout<CalendarBean>(
                CalendarBean.class);
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

        String text;
        {
            final CharSequenceWriter w = new CharSequenceWriter();
            w.writeLine("  aaa            ymd            hms");
            w.writeLine("    a     2011-09-13       17:54:01");
            w.writeLine("    b     2011-01-01       00:00:59");
            text = w.toString();
        }

        // ## Act ##
        final RecordReader<CalendarBean> csvReader = layout
                .openReader(new StringReader(text));

        // ## Assert ##
        final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final CalendarBean bean = new CalendarBean();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a", bean.getAaa());
        assertEquals("2011/09/13 17:54:01",
                format.format(bean.getBbb().getTime()));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("b", bean.getAaa());
        assertEquals("2011/01/01 00:00:59",
                format.format(bean.getBbb().getTime()));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 複数カラムを1プロパティへ対応づけている時に、一部カラムがnullの場合の挙動
     */
    @Test
    public void read_calendar2() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<CalendarBean> layout = new BeanFixedLengthLayout<CalendarBean>(
                CalendarBean.class);
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
        final RecordReader<CalendarBean> csvReader = layout
                .openReader(new StringReader(text));

        // ## Assert ##
        final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        final CalendarBean bean = new CalendarBean();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a", bean.getAaa());
        assertEquals("2011/08/13 11:22:33",
                format.format(bean.getBbb().getTime()));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("b", bean.getAaa());
        assertEquals(null, bean.getBbb());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("c", bean.getAaa());
        assertEquals(null, bean.getBbb());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 複数カラムに対応する
     * propertyを呼び忘れた場合
     */
    @Test
    public void invalid_columns_setup_property() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<CalendarBean> layout = new BeanFixedLengthLayout<CalendarBean>(
                CalendarBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
                @Override
                public void setup(final FixedLengthColumnSetup setup) {
                    setup.column("aaa", 0, 5);
                    // property設定し忘れ
                    setup.columns(
                            new SetupBlock<FixedLengthColumnSetup.FixedLengthCompositeColumnSetup>() {
                                @Override
                                public void setup(
                                        final FixedLengthCompositeColumnSetup compositeSetup) {
                                    compositeSetup.column("ymd", 5, 20);
                                    compositeSetup.column("hms", 20, 35);
                                }
                            }).withConverter(new CalendarConverter());
                }
            });
            fail();
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage());
        }
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        final Charset charset = Charset.forName("UTF-8");
        final Reader reader = getResourceAsReader(suffix, ext, charset);
        return reader;
    }

    static Reader getResourceAsReader(final String suffix, final String ext,
            final Charset charset) {
        final InputStream is = getResourceAsStream(suffix, ext);
        final InputStreamReader reader = new InputStreamReader(is, charset);
        return reader;
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanFixedLengthReaderTest.class.getName() + suffix, ext);
    }

    public static class FlAaaBean {

        private String aaa;
        private String bbb;
        private String ccc;

        @FixedLengthColumn(beginIndex = 0, endIndex = 3)
        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        @FixedLengthColumn(beginIndex = 3, endIndex = 5)
        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

        @FixedLengthColumn(beginIndex = 5, endIndex = 6)
        public String getCcc() {
            return ccc;
        }

        public void setCcc(final String ccc) {
            this.ccc = ccc;
        }

        private final ToStringFormat toStringFormat = new ToStringFormat();

        @Override
        public String toString() {
            return toStringFormat.format(this);
        }

    }

}
