package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.ToStringFormat;

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

    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-1", "tsv");

        final BeanCsvReader<AaaBean> csvReader = new BeanCsvReader<AaaBean>(
            AaaBean.class);

        // ## Act ##
        csvReader.open(new InputStreamReader(is, "UTF-8"));
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
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-2", "tsv");

        final ColumnLayout layout = new ColumnLayout();
        //layout.setNames(new String[] { "あ", "ううう", "いい" });
        layout.addAlias("あ", "aaa");
        layout.addAlias("いい", "bbb");
        layout.addAlias("ううう", "ccc");

        final BeanCsvReader<AaaBean> csvReader = new BeanCsvReader<AaaBean>(
            AaaBean.class, layout);

        // ## Act ##
        csvReader.open(new InputStreamReader(is, "UTF-8"));
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
