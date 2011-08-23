package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.ToStringFormat;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvReader.CustomLayout;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
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

    /**
     * CSVヘッダがBeanのプロパティ名と同じ場合。
     * 
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = getResourceAsStream("-1", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead1(csvReader, bean);
    }

    public static void assertRead1(final CsvReader<AaaBean> csvReader,
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

    /**
     * ヘッダがBeanのプロパティ名と異なる場合。
     * ヘッダ名とbeanのプロパティ名をマッピングすること。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = getResourceAsStream("-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("bbb", "いい");
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
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
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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

    public static void assertRead2(final CsvReader<AaaBean> csvReader,
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
        final InputStream is = getResourceAsStream("-4", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead3(csvReader, bean);
    }

    public static void assertRead3(final CsvReader<AaaBean> csvReader,
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
        final InputStream is = getResourceAsStream("-4", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-3", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
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
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadNoheader(csvReader, bean);
    }

    static void assertReadNoheader(final CsvReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {
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
        final InputStream is = getResourceAsStream("-3", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setWithHeader(false);

        // ## Act ##
        try {
            layout.openReader(new InputStreamReader(is, "UTF-8"));
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
        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        layout.setWithHeader(false);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
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
        final InputStream is = getResourceAsStream("-5", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadEmptyRow(csvReader, bean);
    }

    static void assertReadEmptyRow(final CsvReader<AaaBean> csvReader,
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
        final InputStream is = getResourceAsStream("-5", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        assertReadEmptyRow2(csvReader);
    }

    static void assertReadEmptyRow2(final CsvReader<AaaBean> csvReader)
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
        final InputStream is = getResourceAsStream("-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead4(csvReader, bean);
    }

    public static void assertRead4(final CsvReader<AaaBean> csvReader,
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
        final InputStream is = getResourceAsStream("-6", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc", "ddd");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertRead5(csvReader, bean);
    }

    public static void assertRead5(final CsvReader<AaaBean> csvReader,
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
     * CSV側の列がsetupした列より少ない場合、
     * CSV側に無い項目はnullセットされること。
     */
    @Test
    public void read_smallColumns() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-9", "tsv",
                Charset.forName("UTF-8"));

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("bbb");
                setup.column("ccc");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(r);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadSmallColumns(csvReader, bean);
    }

    static void assertReadSmallColumns(final CsvReader<AaaBean> csvReader,
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
        final InputStream is = getResourceAsStream("-7", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setCustomLayout(new TestLayout());

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadCustomLayout(csvReader, bean);
    }

    public static void assertReadCustomLayout(
            final CsvReader<AaaBean> csvReader, final AaaBean bean)
            throws IOException {
        // tsvデータ部分は1と同じ
        assertRead1(csvReader, bean);
    }

    static class TestLayout implements CustomLayout {

        // 1オリジン
        private int lineNo;

        @Override
        public String[] readRecord(final CsvElementReader elementReader) {
            if (lineNo == 0) {
                // 3行あるheader部をskip
                read0(elementReader);
                read0(elementReader);
                read0(elementReader);
            }

            final String[] rawRecord = read0(elementReader);
            // EOFまで進んでいたらnull
            if (null == rawRecord) {
                return null;
            }

            // footerエリアはskip
            final int validLength = validLength(rawRecord);
            if (validLength == 0 || validLength == 1) {
                return readRecord(elementReader);
            }

            // データ部は、2列目以降
            final String[] customRecord = new String[rawRecord.length - 1];
            System.arraycopy(rawRecord, 1, customRecord, 0,
                    rawRecord.length - 1);

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

        private String[] read0(final CsvElementReader elementReader) {
            lineNo++;
            return elementReader.readRecord();
        }

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
        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
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
        final InputStream is = getResourceAsStream("-1", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadAfterLast(csvReader, bean);
    }

    public static <T> void assertReadAfterLast(final CsvReader<T> csvReader,
            final T bean) {
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
    }

    @Test
    public void readCsv() throws Throwable {
        // ## Arrange ##
        final Reader reader = getResourceAsReader("-8", "csv",
                Charset.forName("UTF-8"));
        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(reader);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        assertReadCsv(csvReader, bean);
    }

    static void assertReadCsv(final CsvReader<AaaBean> csvReader,
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

    static Reader getResourceAsReader(final String suffix, final String ext,
            final Charset charset) {
        final InputStream is = getResourceAsStream(suffix, ext);
        return new InputStreamReader(is, charset);
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

}
