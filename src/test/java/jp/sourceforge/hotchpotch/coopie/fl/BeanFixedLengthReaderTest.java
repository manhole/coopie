package jp.sourceforge.hotchpotch.coopie.fl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
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
        final InputStream is = getResourceAsStream("-1", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        // ## Assert ##
        try {
            layout.openReader(new InputStreamReader(is, "UTF-8"));
            fail();
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage());
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
        final InputStream is = getResourceAsStream("-1", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-2", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-3", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-4", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-5", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-2", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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
        final InputStream is = getResourceAsStream("-1", "tsv");

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
        final RecordReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

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

    @Test
    public void read_annotation_1() throws Throwable {
        // ## Arrange ##
        final BeanFixedLengthLayout<FlAaaBean> layout = new BeanFixedLengthLayout<FlAaaBean>(
                FlAaaBean.class);

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
