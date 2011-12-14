package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

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

        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
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

        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-4-2", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
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

        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-3", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
        assertEquals(expected, actual);
    }

    /*
     * TODO 定義した長さよりも実際のデータが長い場合
     */

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanFixedLengthWriterTest.class.getName() + suffix, ext);
    }

}
