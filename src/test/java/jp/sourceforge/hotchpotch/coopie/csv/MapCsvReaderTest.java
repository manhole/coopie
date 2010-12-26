package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
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

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead1(csvReader, bean);
    }

    static void assertRead1(final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.get("aaa"));
        assertEquals("い3", bean.get("bbb"));
        assertEquals("う3", bean.get("ccc"));

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

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead2(csvReader, bean);
    }

    static void assertRead2(final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

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
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-4", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead3(csvReader, bean);
    }

    static void assertRead3(final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals(" ", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
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

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        assertRead3_2(csvReader);
    }

    static void assertRead3_2(final CsvReader<Map<String, String>> csvReader)
            throws IOException {
        {
            assertEquals(true, csvReader.hasNext());
            final Map<String, String> bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals("あ1", bean.get("aaa"));
            assertEquals("い1", bean.get("bbb"));
            assertEquals(" ", bean.get("ccc"));
        }
        {
            assertEquals(true, csvReader.hasNext());
            final Map<String, String> bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals(null, bean.get("aaa"));
            assertEquals("い2", bean.get("bbb"));
            assertEquals(null, bean.get("ccc"));
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

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadNoheader(csvReader, bean);
    }

    static void assertReadNoheader(
            final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {
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

    /**
     * 空ファイルの場合。
     * (ヘッダなし)
     */
    @Test
    public void read_empty() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setWithHeader(false);

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader(""));

        // ## Assert ##
        assertEquals(false, csvReader.hasNext());

        csvReader.close();
    }

    /**
     * 空ファイルの場合。
     * (ヘッダなし、ヘッダ名指定有り ... この組み合わせが既におかしいが...)
     */
    @Test
    public void read_empty2() throws Throwable {
        // ## Arrange ##
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
                .openReader(new StringReader(""));

        // ## Assert ##
        assertEquals(false, csvReader.hasNext());

        csvReader.close();
    }

    /**
     * 空ファイルの場合。
     * (ヘッダあり)
     */
    @Test
    public void read_empty3() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setWithHeader(true);

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
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
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-5", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadEmptyRow(csvReader, bean);
    }

    static void assertReadEmptyRow(
            final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(null, bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.get("aaa"));
        assertEquals("い3", bean.get("bbb"));
        assertEquals("う3", bean.get("ccc"));

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
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-5", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        assertReadEmptyRow2(csvReader);
    }

    static void assertReadEmptyRow2(
            final CsvReader<Map<String, String>> csvReader) throws IOException {

        {
            assertEquals(true, csvReader.hasNext());
            final Map<String, String> bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals("あ1", bean.get("aaa"));
            assertEquals("い1", bean.get("bbb"));
            assertEquals("う1", bean.get("ccc"));

        }

        {
            assertEquals(true, csvReader.hasNext());
            final Map<String, String> bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals(null, bean.get("aaa"));
            assertEquals(null, bean.get("bbb"));
            assertEquals(null, bean.get("ccc"));
        }

        {
            assertEquals(true, csvReader.hasNext());
            final Map<String, String> bean = csvReader.read();
            logger.debug(bean.toString());
            assertEquals("あ3", bean.get("aaa"));
            assertEquals("い3", bean.get("bbb"));
            assertEquals("う3", bean.get("ccc"));
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
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-2", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead4(csvReader, bean);
    }

    static void assertRead4(final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read5() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-6", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc", "ddd");
            }
        });

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead5(csvReader, bean);
    }

    static void assertRead5(final CsvReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals("え1", bean.get("ccc"));
        assertEquals(null, bean.get("ddd"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals("え2", bean.get("ccc"));
        assertEquals(null, bean.get("ddd"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

}
