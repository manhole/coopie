package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.SkipEmptyLineReadEditor;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.TestReadEditor;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public class MapCsvReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read_open_null() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();

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

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead1(csvReader, bean);
    }

    public static void assertRead1(
            final RecordReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(0, csvReader.getRecordNumber());
        assertEquals(true, csvReader.hasNext());
        assertEquals(0, csvReader.getRecordNumber());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(1, csvReader.getRecordNumber());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(2, csvReader.getRecordNumber());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals("い2", bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals(3, csvReader.getRecordNumber());
        assertEquals("あ3", bean.get("aaa"));
        assertEquals("い3", bean.get("bbb"));
        assertEquals("う3", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        assertEquals(3, csvReader.getRecordNumber());
        csvReader.close();
    }

    /**
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-2", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
                setup.column("bbb", "いい");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead2(csvReader, bean);
    }

    public static void assertRead2(
            final RecordReader<Map<String, String>> csvReader,
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
        final Reader r = getResourceAsReader("-4", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead3(csvReader, bean);
    }

    static void assertRead3(final RecordReader<Map<String, String>> csvReader,
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
        final Reader r = getResourceAsReader("-4", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        assertRead3_2(csvReader);
    }

    static void assertRead3_2(final RecordReader<Map<String, String>> csvReader)
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
        final Reader r = getResourceAsReader("-3", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
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
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadNoheader(csvReader, bean);
    }

    public static void assertReadNoheader(
            final RecordReader<Map<String, String>> csvReader,
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
        final Reader r = getResourceAsReader("-3", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
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
     * (ヘッダなし)
     */
    @Test
    public void read_empty() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setWithHeader(false);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
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
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
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
        final RecordReader<Map<String, String>> csvReader = layout
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
        final RecordReader<Map<String, String>> csvReader = layout
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

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadEmptyRow(csvReader, bean);
    }

    public static void assertReadEmptyRow(
            final RecordReader<Map<String, String>> csvReader,
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
        final Reader r = getResourceAsReader("-5", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        assertReadEmptyRow2(csvReader);
    }

    static void assertReadEmptyRow2(
            final RecordReader<Map<String, String>> csvReader)
            throws IOException {

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
        final Reader r = getResourceAsReader("-2", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead4(csvReader, bean);
    }

    public static void assertRead4(
            final RecordReader<Map<String, String>> csvReader,
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
        final Reader r = getResourceAsReader("-6", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc", "ddd");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead5(csvReader, bean);
    }

    static void assertRead5(final RecordReader<Map<String, String>> csvReader,
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

    /**
     * CSV側の列がsetupした列より少ない場合、
     * CSV側に無い項目はnullセットされること。
     */
    @Test
    public void read_smallColumns() throws Throwable {
        // ## Arrange ##
        final Reader r = BeanCsvReaderTest.getResourceAsReader("-9", "tsv",
                Charset.forName("UTF-8"));

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("bbb");
                setup.column("ccc");
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadSmallColumns(csvReader, bean);
    }

    static void assertReadSmallColumns(
            final RecordReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals("う1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        // ファイルにbbbは無いため、nullで上書きされること
        bean.put("aaa", "zz1");
        bean.put("bbb", "zz2");
        bean.put("ccc", "zz3");
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.get("aaa"));
        assertEquals(null, bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

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

        final MapCsvLayout layout = new MapCsvLayout();
        final TestReadEditor elementReaderHandler = new TestReadEditor();
        layout.setElementReaderHandler(elementReaderHandler);
        layout.setLineReaderHandler(elementReaderHandler);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadCustomLayout(csvReader, bean);
    }

    public static void assertReadCustomLayout(
            final RecordReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {
        // tsvデータ部分は1と同じ
        assertRead1(csvReader, bean);
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final Reader r = getResourceAsReader("-1", "tsv");

        final MapCsvLayout layout = new MapCsvLayout();

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        BeanCsvReaderTest.assertReadAfterLast(csvReader, bean);
    }

    @Test
    public void readCsv() throws Throwable {
        // ## Arrange ##
        final Reader reader = BeanCsvReaderTest.getResourceAsReader("-8",
                "csv", Charset.forName("UTF-8"));

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(reader);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertReadCsv(csvReader, bean);
    }

    static void assertReadCsv(
            final RecordReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a1", bean.get("aaa"));
        assertEquals("b1", bean.get("bbb"));
        assertEquals("c1", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a2", bean.get("aaa"));
        assertEquals("b2", bean.get("bbb"));
        assertEquals("c2", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    @Test
    public void read_separator_comma() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_SEPARATOR_COMMA));

        // ## Assert ##
        _assert(csvReader);
    }

    @Test
    public void read_separator_tab() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.TAB);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_SEPARATOR_TAB));

        // ## Assert ##
        _assert(csvReader);
    }

    @Test
    public void read_lineseparator_LF() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setLineSeparator("\n");

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_LINESEPARATOR_LF));

        // ## Assert ##
        _assert(csvReader);
    }

    @Test
    public void read_quotechar_single() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setQuoteMark('\'');

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader(
                        BeanCsvWriterTest.V_WRITE_QUOTECHAR_SINGLE));

        // ## Assert ##
        _assert(csvReader);
    }

    private void _assert(final RecordReader<Map<String, String>> csvReader)
            throws IOException {
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("a1", bean.get("a"));
        assertEquals("b1", bean.get("b"));
        assertEquals("c1", bean.get("c"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * デフォルトではtrimしない。
     */
    @Test
    public void read_trim_off() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader("aaa,bbb,ccc\n" + "  , b , \n"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals("  ", bean.get("aaa"));
        assertEquals(" b ", bean.get("bbb"));
        assertEquals(" ", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 要素をtrimするオプション
     */
    @Test
    public void read_trim_all() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setElementEditor(ElementEditors.trim());

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader("aaa,bbb,ccc\n" + "  , b　  , \n"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals(null, bean.get("aaa"));
        assertEquals("b　", bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    /**
     * 全角スペースもtrim対象とするオプション
     */
    @Test
    public void read_trim_all_whitespace() throws Throwable {
        // ## Arrange ##
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setElementSeparator(CsvSetting.COMMA);
        layout.setElementEditor(ElementEditors.trimWhitespace());

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader("aaa,bbb,ccc\n" + "  , b　  , \n"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        assertEquals(null, bean.get("aaa"));
        assertEquals("b", bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

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

        final MapCsvLayout layout = new MapCsvLayout();
        layout.setLineReaderHandler(new SkipEmptyLineReadEditor());

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(r);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
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
        assertEquals("い\r\n\r\n\r\n2", bean.get("bbb"));
        assertEquals("う2", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

    static Reader getResourceAsReader(final String suffix, final String ext) {
        final Reader reader = BeanCsvReaderTest
                .getResourceAsReader(suffix, ext);
        return reader;
    }

}
