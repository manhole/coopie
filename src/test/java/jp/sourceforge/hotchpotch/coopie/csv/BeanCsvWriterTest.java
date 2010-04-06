package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;

import org.junit.Test;
import org.t2framework.commons.util.ReaderUtil;
import org.t2framework.commons.util.ResourceUtil;

public class BeanCsvWriterTest {

    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
            AaaBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final BeanCsvWriter<AaaBean> csvWriter = layout.openWriter(writer);

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

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvWriterTest.class.getName() + "-1", "tsv");
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
        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
            AaaBean.class);
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
            }
        });

        // ## Act ##
        final StringWriter writer = new StringWriter();
        final BeanCsvWriter<AaaBean> csvWriter = layout.openWriter(writer);

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

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
            "UTF-8"));
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void write3() throws Throwable {
        // ## Arrange ##
        final BeanCsvLayout<AaaBean> layout = new BeanCsvLayout<AaaBean>(
            AaaBean.class);
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
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
        final BeanCsvWriter<AaaBean> csvWriter = layout.openWriter(writer);

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

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-2", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
            "UTF-8"));
        assertEquals(expected, actual);
    }

}
