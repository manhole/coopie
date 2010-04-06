package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
            .openReader(new InputStreamReader(is, "UTF-8"));

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

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
                setup.column("bbb", "いい");
            }
        });

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
            .openReader(new InputStreamReader(is, "UTF-8"));

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

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-3", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                /*
                 * CSVの列順
                 */
                setup.column("ccc");
                setup.column("aaa");
                setup.column("bbb");
            }
        });

        layout.setWithHeader(false);

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
            .openReader(new InputStreamReader(is, "UTF-8"));

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

    /**
     * CSVヘッダがない場合は、必ず列順を設定すること。
     * 設定していない場合は例外とする。
     */
    @Test
    public void read_noheader_badsetting() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-3", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setWithHeader(false);

        // ## Act ##
        try {
            layout.openReader(new InputStreamReader(is, "UTF-8"));
            fail();
        } catch (final IllegalStateException e) {
            logger.debug(e.getMessage());
        }
    }

}
