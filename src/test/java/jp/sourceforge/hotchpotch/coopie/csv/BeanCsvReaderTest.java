package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout.PropertyNotFoundException;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvWriterTest.AaaBeanBasicSetup;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;
import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.exception.ParseRuntimeException;
import org.t2framework.commons.util.ResourceUtil;

/*
 * CSVの設定としては、
 * - JavaBeanクラス
 * - 改行文字(writerのみ)
 * - 区切り文字: tabかカンマか
 * - CSVヘッダの有無
 * - CSV項目の順序
 *   ヘッダがある場合は、read時は不要。
 * - ""のときに、nullにするか、""にするか。
 */
public class BeanCsvReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read_open_null() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

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
     * CSVヘッダがBeanのプロパティ名と同じ場合。
     * 
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead1(csvReader, bean);
    }

    public static void assertRead1(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(0, csvReader.getRecordNumber());
        assertEquals(true, csvReader.hasNext());
        assertEquals(0, csvReader.getRecordNumber());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(1, csvReader.getRecordNumber());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(2, csvReader.getRecordNumber());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(3, csvReader.getRecordNumber());
        assertEquals("あ3", bean.getAaa());
        assertEquals("い3", bean.getBbb());
        assertEquals("う3", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        assertEquals(3, csvReader.getRecordNumber());
        csvReader.close();
    }

    /**
     * ヘッダがBeanのプロパティ名と異なる場合。
     * ヘッダ名とbeanのプロパティ名をマッピングすること。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("bbb", "いい");
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead2(csvReader, bean);
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
        final Reader r = getResourceAsReader("-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column(new LazyColumnName("bbb") {
                    @Override
                    public boolean labelEquals(final String label) {
                        return label.contains("い");
                    }
                });
                setup.column(new LazyColumnName("aaa") {
                    @Override
                    public boolean labelEquals(final String label) {
                        return label.contains("あ");
                    }
                });
                setup.column(new LazyColumnName("ccc") {
                    @Override
                    public boolean labelEquals(final String label) {
                        return label.contains("う");
                    }
                });
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead2(csvReader, bean);
    }

    static class LazyColumnName extends SimpleColumnName {

        public LazyColumnName(final String labelAndName) {
            setLabel(labelAndName);
            setName(labelAndName);
        }

        @Override
        public boolean labelEquals(final String label) {
            throw new AssertionError("should override");
        }

    }

    public static void assertRead2(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 空白項目がある場合。
     * 
     * ""はnullとして扱い、" "は" "として扱う。
     */
    @Test
    public void read3() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-4", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead3(csvReader, bean);
    }

    public static void assertRead3(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals(" ", bean.getCcc());

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
     * recordインスタンスをCsvReaderに生成させる。
     */
    @Test
    public void read3_2() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-4", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        {
            assertEquals(true, csvReader.hasNext());
            final AaaBean bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals("あ1", bean.getAaa());
            assertEquals("い1", bean.getBbb());
            assertEquals(" ", bean.getCcc());
        }
        {
            assertEquals(true, csvReader.hasNext());
            final AaaBean bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals(null, bean.getAaa());
            assertEquals("い2", bean.getBbb());
            assertEquals(null, bean.getCcc());
        }

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-3", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * CSVの列順
                 */
                setup.column("ccc", "ううう");
                setup.column("aaa", "あ");
                setup.column("bbb", "いい");
            }
        });

        layout.setWithHeader(false);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadNoheader(csvReader, bean);
    }

    public static void assertReadNoheader(
            final RecordReader<AaaBean> csvReader, final AaaBean bean)
            throws IOException {
        /*
         * データはread2のテストと同じなので。
         */
        assertRead2(csvReader, bean);
    }

    /**
     * CSVヘッダがない場合は、必ず列順を設定すること。
     * 設定していない場合は例外とする。
     */
    @Test
    public void read_noheader_badsetting() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-3", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setWithHeader(false);

        // ## Act ##
        try {
            layout.openReader(r);
            fail();
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * 空ファイルの場合。
     */
    @Test
    public void read_empty() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

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
     */
    @Test
    public void read_empty_row() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-5", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadEmptyRow(csvReader, bean);
    }

    public static void assertReadEmptyRow(
            final RecordReader<AaaBean> csvReader, final AaaBean bean)
            throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals(null, bean.getCcc());

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
     * 空行がある場合。
     * 
     * 各要素を"" (null)として扱う。
     * 
     * recordインスタンスをCsvReaderに生成させる。
     */
    @Test
    public void read_empty_row_2() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-5", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        assertReadEmptyRow2(csvReader);
    }

    static void assertReadEmptyRow2(final RecordReader<AaaBean> csvReader)
            throws IOException {

        {
            assertEquals(true, csvReader.hasNext());
            final AaaBean bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals("あ1", bean.getAaa());
            assertEquals("い1", bean.getBbb());
            assertEquals("う1", bean.getCcc());
        }

        {
            assertEquals(true, csvReader.hasNext());
            final AaaBean bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals(null, bean.getAaa());
            assertEquals(null, bean.getBbb());
            assertEquals(null, bean.getCcc());
        }

        {
            assertEquals(true, csvReader.hasNext());
            final AaaBean bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals("あ3", bean.getAaa());
            assertEquals("い3", bean.getBbb());
            assertEquals("う3", bean.getCcc());
        }

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read4() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead4(csvReader, bean);
    }

    public static void assertRead4(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read5() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-6", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc", "ddd");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead5(csvReader, bean);
    }

    public static void assertRead5(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals("え1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals("え2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setupしたプロパティ名がJavaBeansに無い場合は、例外とする。
     */
    @Test
    public void invalid_propertyName() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
                @Override
                public void setup(final CsvColumnSetup setup) {
                    setup.column("bad_property_name");
                }
            });
            fail();
        } catch (final PropertyNotFoundException e) {
            logger.debug(e.getMessage());
        }
    }

    /**
     * CSV側の列がsetupした列より少ない場合、
     * CSV側に無い項目はnullセットされること。
     */
    @Test
    public void read_smallColumns() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-9", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("bbb");
                setup.column("ccc");
            }
        });

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadSmallColumns(csvReader, bean);
    }

    static void assertReadSmallColumns(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        // ファイルにbbbは無いため、nullで上書きされること
        bean.setAaa("zz1");
        bean.setBbb("zz2");
        bean.setCcc("zz3");
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals(null, bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 独自レイアウトのtsvファイルを入力する。
     * 
     * - header部が3行
     * - footer部が2行
     * - データ部は2列目から
     * という想定。
     */
    @Test
    public void read_customLayout() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-7", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        final TestReadEditor readEditor = new TestReadEditor();
        layout.setReaderHandler(readEditor);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadCustomLayout(csvReader, bean);
    }

    public static void assertReadCustomLayout(
            final RecordReader<AaaBean> csvReader, final AaaBean bean)
            throws IOException {
        // tsvデータ部分は1と同じ
        assertRead1(csvReader, bean);
    }

    static class TestReadEditor extends DefaultReaderHandler {

        @Override
        public Line readLine(final LineReader lineReader,
                final Line reusableLine) throws IOException {
            if (lineReader.getLineNumber() == 0) {
                // 3行あるheader部をskip
                lineReader.readLine();
                lineReader.readLine();
                lineReader.readLine();
            }
            final Line line = super.readLine(lineReader, reusableLine);
            return line;
        }

        @Override
        public String[] readRecord(final ElementReader elementReader) {
            final String[] rawRecord = elementReader.readRecord();
            // EOFまで進んでいたらnull
            if (null == rawRecord) {
                return null;
            }

            /*
             * footerエリアはskip
             * (ここでは、データ部は必ず2つ以上要素がある想定)
             */
            final int validLength = validLength(rawRecord);
            if (validLength == 0 || validLength == 1) {
                return readRecord(elementReader);
            }

            // データ部は、2列目以降
            final String[] customRecord = Arrays.copyOfRange(rawRecord, 1,
                    rawRecord.length);

            return customRecord;
        }

        /*
         * 配列末尾のnullを除いた長さを返します。
         * [10, 20, 30, null, null] => 3
         * [null, null] => 0
         */
        private int validLength(final Object[] arr) {
            int nullCount = 0;
            final int len = arr.length;
            for (int i = len - 1; 0 <= i; i--) {
                final Object obj = arr[i];
                if (obj != null) {
                    break;
                }
                nullCount++;
            }
            return len - nullCount;
        }

    }

    /**
     * 1つのLayoutインスタンスから何度もCsvReaderをopenしたとき、
     * それぞれのReaderで読めること。(前の状態が干渉しないこと)
     */
    @Test
    public void read_moreThanOnce() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("bbb");
                setup.column("ccc");
            }
        });

        // ## Act ##
        // ## Assert ##
        final AaaBean bean = new AaaBean();

        final String ext = "tsv";
        assertRead9_1(layout.openReader(getResourceAsReader("-10-1", ext)),
                bean);
        assertRead9_2(layout.openReader(getResourceAsReader("-10-2", ext)),
                bean);
        assertRead9_1(layout.openReader(getResourceAsReader("-10-3", ext)),
                bean);
        assertRead9_1(layout.openReader(getResourceAsReader("-10-1", ext)),
                bean);
        assertRead9_2(layout.openReader(getResourceAsReader("-10-2", ext)),
                bean);
    }

    private void assertRead9_1(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.getAaa());
        assertEquals("い3", bean.getBbb());
        assertEquals("う3", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    private void assertRead9_2(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals(null, bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 1つのLayoutインスタンスから複数のCsvReaderをopenしたとき、
     * それぞれのReaderでの処理が影響しないこと。
     */
    @Test
    @Ignore
    // FIXME 実装中
    public void openMultiReader() throws Throwable {
        // ## Arrange ##
        final InputStream is1 = getResourceAsStream("-1", "tsv");
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is1, "UTF-8"));
        //logger.debug(ReaderUtil.readText(new InputStreamReader(is, "UTF-8")));

        final AaaBean bean = new AaaBean();
        csvReader.read(bean);

        // ## Assert ##
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.getAaa());
        assertEquals("い3", bean.getBbb());
        assertEquals("う3", bean.getCcc());

        csvReader.close();
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadAfterLast(csvReader, bean);
    }

    public static <T> void assertReadAfterLast(final RecordReader<T> csvReader,
            final T bean) throws Throwable {
        csvReader.read(bean);
        csvReader.read(bean);
        csvReader.read(bean);
        // 3レコードある
        try {
            csvReader.read(bean);
            fail();
        } catch (final NoSuchElementException e) {
            logger.debug(e.getMessage());
        }
        csvReader.close();
    }

    @Test
    public void readCsv() throws Throwable {
        // ## Arrange ##
        final Reader reader = getResourceAsReader("-8", "csv");
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout.openReader(reader);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadCsv(csvReader, bean);
    }

    static void assertReadCsv(final RecordReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a1", bean.getAaa());
        assertEquals("b1", bean.getBbb());
        assertEquals("c1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a2", bean.getAaa());
        assertEquals("b2", bean.getBbb());
        assertEquals("c2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    static String CRLF = CsvSetting.CRLF;
    static String LF = CsvSetting.LF_S;

    @Test
    public void read_separator_comma() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_SEPARATOR_COMMA));

        // ## Assert ##
        _assert(csvReader);
    }

    @Test
    public void read_separator_tab() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.TAB);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_SEPARATOR_TAB));

        // ## Assert ##
        _assert(csvReader);
    }

    @Test
    public void read_lineseparator_LF() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setLineSeparator("\n");

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_LINESEPARATOR_LF));

        // ## Assert ##
        _assert(csvReader);
    }

    @Test
    public void read_quotechar_single() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setupColumns(new AaaBeanBasicSetup());
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setQuoteMark('\'');

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_QUOTECHAR_SINGLE));

        // ## Assert ##
        _assert(csvReader);
    }

    private void _assert(final RecordReader<AaaBean> csvReader)
            throws IOException {
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a1", bean.getAaa());
        assertEquals("b1", bean.getBbb());
        assertEquals("c1", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * デフォルトではtrimしない。
     */
    @Test
    public void read_trim_off() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("aaa,bbb,ccc\n" + "  , b , \n"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("  ", bean.getAaa());
        assertEquals(" b ", bean.getBbb());
        assertEquals(" ", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 要素をtrimするオプション
     */
    @Test
    public void read_trim_all() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setElementEditor(ElementEditors.trim());

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("aaa,bbb,ccc\n" + "  , b　  , \n"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals(null, bean.getAaa());
        assertEquals("b　", bean.getBbb());
        assertEquals(null, bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 全角スペースもtrim対象とするオプション
     */
    @Test
    public void read_trim_all_whitespace() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setElementEditor(ElementEditors.trimWhitespace());

        // ## Act ##
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new StringReader("aaa,bbb,ccc\n" + "  , b　  , \n"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals(null, bean.getAaa());
        assertEquals("b", bean.getBbb());
        assertEquals(null, bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * ヘッダがBeanのプロパティ名と異なる場合。
     * アノテーションからlabelを取得して、ヘッダ名とbeanのプロパティ名をマッピングすること。
     */
    @Test
    public void read_annotation_1() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-2", "tsv");

        final BeanCsvLayout<CccBean> layout = BeanCsvLayout
                .getInstance(CccBean.class);

        // ## Act ##
        final RecordReader<CccBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final CccBean bean = new CccBean();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 空行をskipして読めること。
     * その際に、データ部の改行による空行を除かないこと。
     */
    @Test
    public void read_skip_emptyline() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-11", "tsv");

        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);
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
        assertEquals("あ2", bean.getAaa());
        assertEquals("い\r\n\r\n\r\n2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    public static class SkipEmptyLineReadEditor extends
            DefaultLineReaderHandler {

        @Override
        public boolean acceptLine(final Line line,
                final ElementParserContext parserContext) {
            if (!parserContext.isInElement()) {
                if (line.getBody().length() == 0) {
                    return false;
                }
            }
            return super.acceptLine(line, parserContext);
        }

    }

    /**
     * setReaderHandlerではLineReaderHandlerなど何らかのinterfaceをimplしているべき。
     */
    @Test
    public void setup_invalid_readeditor() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = BeanCsvLayout
                .getInstance(AaaBean.class);

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
        final BeanCsvLayout<BigDecimalBean> layout = new BeanCsvLayout<BigDecimalBean>(
                BigDecimalBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa").converter(new BigDecimalConverter());
                setup.column("bbb");
            }
        });

        // ## Act ##
        final RecordReader<BigDecimalBean> csvReader = layout
                .openReader(getResourceAsReader("-12", "tsv"));

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
     * CSV側が2カラムで、対応するJava側が1プロパティの場合。
     * 年月日と時分秒で列が別れているとする。
     */
    @Test
    public void read_calendar1() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<CalendarBean> layout = new BeanCsvLayout<CalendarBean>(
                CalendarBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                // CSVの"ymd"と"hms"列を、JavaBeanの"bbb"プロパティと対応付ける。
                // 2列 <=> 1プロパティ の変換にConverterを使用する。
                setup.columns("ymd", "hms").property("bbb")
                        .converter(new CalendarConverter());
            }
        });
        final String text;
        {
            final StringWriter sw = new StringWriter();
            final ElementWriter writer = new CsvElementInOut(
                    new DefaultCsvSetting()).openWriter(sw);

            writer.writeRecord(a("aaa", "ymd", "hms"));
            writer.writeRecord(a("a", "2011-09-13", "17:54:01"));
            writer.writeRecord(a("b", "2011-01-01", "00:00:59"));
            writer.close();
            text = sw.toString();
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
        final BeanCsvLayout<CalendarBean> layout = new BeanCsvLayout<CalendarBean>(
                CalendarBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.columns("ymd", "hms").property("bbb")
                        .converter(new CalendarConverter());
            }
        });

        final String text;
        {
            final StringWriter sw = new StringWriter();
            final ElementWriter writer = new CsvElementInOut(
                    new DefaultCsvSetting()).openWriter(sw);
            writer.writeRecord(a("aaa", "ymd", "hms"));
            writer.writeRecord(a("a", "2011-08-13", "11:22:33"));
            writer.writeRecord(a("b", "2011-09-14", ""));
            writer.writeRecord(a("c", "", "12:22:33"));
            writer.close();
            text = sw.toString();
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
                BeanCsvReaderTest.class.getName() + suffix, ext);
    }

    public static class AaaBean {

        private String aaa;
        private String bbb;
        private String ccc;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

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

    public static class BbbBean {

        private String aa;
        private String bb;

        public String getAa() {
            return aa;
        }

        public void setAa(final String aaa) {
            aa = aaa;
        }

        public String getBb() {
            return bb;
        }

        public void setBb(final String bbb) {
            bb = bbb;
        }

        private final ToStringFormat toStringFormat = new ToStringFormat();

        @Override
        public String toString() {
            return toStringFormat.format(this);
        }

    }

    public static class CccBean {

        private String aaa;
        private String bbb;
        private String ccc;

        @CsvColumn(label = "あ")
        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        @CsvColumn(label = "いい")
        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

        @CsvColumn(label = "ううう")
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

    public static class DddBean {

        private String aaa;
        private String bbb;
        private String ccc;

        @CsvColumn(order = 0)
        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        @CsvColumn(order = 1)
        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

        @CsvColumn(order = 2)
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

    public static class EeeBean {

        private String aaa;
        private String bbb;
        private String ccc;

        @CsvColumn(label = "あ", order = 0)
        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        @CsvColumn(label = "いい", order = 2)
        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

        @CsvColumn(label = "ううう", order = 1)
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

    public static class BigDecimalBean {

        private BigDecimal aaa_;
        private String bbb_;

        public BigDecimal getAaa() {
            return aaa_;
        }

        public void setAaa(final BigDecimal aaa) {
            aaa_ = aaa;
        }

        public String getBbb() {
            return bbb_;
        }

        public void setBbb(final String bbb) {
            bbb_ = bbb;
        }

    }

    public static class BigDecimalConverter implements
            Converter<BigDecimal, String> {

        private final DecimalFormat format_;

        public BigDecimalConverter() {
            format_ = (DecimalFormat) NumberFormat.getNumberInstance();
            format_.setMinimumFractionDigits(2);
            format_.setMaximumFractionDigits(2);
            format_.setGroupingUsed(true);
            format_.setGroupingSize(3);
            format_.setParseBigDecimal(true);
        }

        @Override
        public String convertTo(final BigDecimal from) {
            if (from == null) {
                return null;
            }
            final String s = format_.format(from);
            return s;
        }

        @Override
        public BigDecimal convertFrom(final String from) {
            if (from == null) {
                return null;
            }
            final ParsePosition pp = new ParsePosition(0);
            final Number parse = format_.parse(from, pp);
            if (parse == null || pp.getIndex() != from.length()) {
                // パースエラー
                throw new ParseRuntimeException(new ParseException(from,
                        pp.getIndex()));
            }
            final BigDecimal o = (BigDecimal) parse;
            return o;
        }

    }

    public static class CalendarBean {

        private String aaa_;
        private Calendar bbb_;

        public String getAaa() {
            return aaa_;
        }

        public void setAaa(final String aaa) {
            aaa_ = aaa;
        }

        public Calendar getBbb() {
            return bbb_;
        }

        public void setBbb(final Calendar bbb) {
            bbb_ = bbb;
        }

    }

    public static class CalendarConverter implements
            Converter<Calendar, String[]> {

        private final DateFormat dayFormat_;
        private final DateFormat timeFormat_;

        public CalendarConverter() {
            dayFormat_ = new SimpleDateFormat("yyyy-MM-dd");
            timeFormat_ = new SimpleDateFormat("HH:mm:ss");
        }

        /*
         * Calendarがnullの場合は年月日と時分秒をどちらもnullとする
         */
        @Override
        public String[] convertTo(final Calendar from) {
            final Calendar o = from;
            final String[] to = new String[2];
            if (o == null) {
                return to;
            }

            final Date date = o.getTime();
            {
                final String s = dayFormat_.format(date);
                to[0] = s;
            }
            {
                final String s = timeFormat_.format(date);
                to[1] = s;
            }
            return to;
        }

        /*
         * 年月日と時分秒の一方でもnullの場合はCalendarをnullとする
         */
        @Override
        public Calendar convertFrom(final String[] from) {

            final Calendar cal = Calendar.getInstance();
            cal.clear();
            {
                final String s = from[0];
                if (s == null) {
                    return null;
                }
                final Date date = parse(s, dayFormat_);
                final Calendar c = Calendar.getInstance();
                c.setTime(date);
                copy(c, cal, Calendar.YEAR);
                copy(c, cal, Calendar.MONTH);
                copy(c, cal, Calendar.DATE);
            }
            {
                final String s = from[1];
                if (s == null) {
                    return null;
                }
                final Date date = parse(s, timeFormat_);
                final Calendar c = Calendar.getInstance();
                c.setTime(date);
                copy(c, cal, Calendar.HOUR_OF_DAY);
                copy(c, cal, Calendar.MINUTE);
                copy(c, cal, Calendar.SECOND);
            }
            return cal;
        }

        private void copy(final Calendar src, final Calendar dest,
                final int field) {
            final int v = src.get(field);
            dest.set(field, v);
        }

        private Date parse(final String s, final DateFormat format) {
            final ParsePosition pp = new ParsePosition(0);
            final Date date = format.parse(s, pp);
            if (date == null || pp.getIndex() != s.length()) {
                // パースエラー
                throw new ParseRuntimeException(new ParseException(s,
                        pp.getIndex()));
            }
            return date;
        }

    }

}
