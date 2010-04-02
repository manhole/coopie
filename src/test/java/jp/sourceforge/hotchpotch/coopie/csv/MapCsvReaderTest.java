package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ResourceUtil;

public class MapCsvReaderTest {

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

        final MapCsvReader csvReader = new MapCsvReader();

        // ## Act ##
        csvReader.open(new InputStreamReader(is, "UTF-8"));

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        csvReader.read(bean);

        // ## Assert ##
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.get("aaa"));
        assertEquals("い3", bean.get("bbb"));
        assertEquals("う3", bean.get("ccc"));

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

        final MapColumnLayout layout = new MapColumnLayout();
        layout.setupColumns(new ColumnSetup() {
            @Override
            public void setup() {
                column("aaa", "あ");
                column("ccc", "ううう");
                column("bbb", "いい");
            }
        });

        final MapCsvReader csvReader = new MapCsvReader(layout);

        // ## Act ##
        csvReader.open(new InputStreamReader(is, "UTF-8"));

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        csvReader.read(bean);

        // ## Assert ##
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

        csvReader.close();
    }

}
