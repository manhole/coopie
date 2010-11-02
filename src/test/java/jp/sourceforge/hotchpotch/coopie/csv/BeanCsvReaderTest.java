package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.ToStringFormat;

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

    /*
     * TODO
     * 末端まで達した後のreadでは、例外が発生すること。
     */

    /**
     * CSVヘッダがBeanのプロパティ名と同じ場合。
     * 
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-1", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        final AaaBean bean = new AaaBean();

        // ## Assert ##
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
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-2", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
                setup.column("bbb", "いい");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        final AaaBean bean = new AaaBean();

        // ## Assert ##
        assertRead2(csvReader, bean);
    }

    public static void assertRead2(final CsvReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

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
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-4", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        final AaaBean bean = new AaaBean();

        // ## Assert ##
        assertRead3(csvReader, bean);
    }

    public static void assertRead3(final CsvReader<AaaBean> csvReader,
            final AaaBean bean) throws IOException {
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals(" ", bean.getCcc());

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals(null, bean.getCcc());

        csvReader.close();
    }

    /**
     * recordインスタンスをCsvReaderに生成させる。
     */
    @Test
    public void read3_2() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-4", "tsv");

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
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-3", "tsv");

        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
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

        final AaaBean bean = new AaaBean();
        assertRead2(csvReader, bean);
    }

    /**
     * CSVヘッダがない場合は、必ず列順を設定すること。
     * 設定していない場合は例外とする。
     */
    @Test
    public void read_noheader_badsetting() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-3", "tsv");

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
     * 1つのLayoutインスタンスから複数のCsvReaderをopenしたとき、
     * それぞれのReaderでの処理が影響しないこと。
     */
    @Test
    @Ignore
    // FIXME 実装中
    public void openMultiReader() throws Throwable {
        // ## Arrange ##
        final InputStream is1 = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-1", "tsv");
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

}
