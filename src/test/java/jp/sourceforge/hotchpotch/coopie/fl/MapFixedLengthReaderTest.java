package jp.sourceforge.hotchpotch.coopie.fl;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest;
import jp.sourceforge.hotchpotch.coopie.csv.MapCsvReaderTest;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public class MapFixedLengthReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * ファイルヘッダがBeanのプロパティ名と同じ場合。
     * 
     * ※固定長ファイルでは、ヘッダがあっても大事に扱わない。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
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
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead1(csvReader, bean);
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
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-2", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
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
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead2(csvReader, bean);
    }

    /**
     * ファイルヘッダが無い場合。
     * 
     * ※これが通常の固定長ファイル
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-3", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("ccc", 0, 6);
                setup.column("aaa", 6, 12);
                setup.column("bbb", 12, 20);
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadNoheader(csvReader, bean);
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
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-4", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
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
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        assertRead3(csvReader, bean);
    }

    private static void assertRead3(
            final RecordReader<Map<String, String>> csvReader,
            final Map<String, String> bean) throws IOException {

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("あ1", bean.get("aaa"));
        assertEquals("い1", bean.get("bbb"));
        //assertEquals(" ", bean.getCcc());
        assertEquals(null, bean.get("ccc"));

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
     * 空ファイルの場合。
     */
    @Test
    public void read_empty() throws Throwable {
        // ## Arrange ##
        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                // 空ファイルなので、ここは何でも良い
                setup.column("aaa", 0, 7);
                setup.column("bbb", 7, 14);
                setup.column("ccc", 14, 20);
            }
        });

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
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-5", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
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
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadEmptyRow(csvReader, bean);
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read4() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-2", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
            }
        });
        layout.setWithHeader(true);

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead4(csvReader, bean);
    }

    /**
     * 末端まで達した後のreadでは、例外が発生すること。
     */
    @Test
    public void read_afterLast() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");

        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
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
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
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
        final MapFixedLengthLayout layout = new MapFixedLengthLayout();
        layout.setupColumns(new SetupBlock<FixedLengthColumnSetup>() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 3);
                setup.column("bbb", 3, 6);
                setup.column("ccc", 6, 9);
            }
        });

        // ## Act ##
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(new StringReader("111222333\n44455\n666777888\n"));

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("111", bean.get("aaa"));
        assertEquals("222", bean.get("bbb"));
        assertEquals("333", bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("444", bean.get("aaa"));
        assertEquals("55", bean.get("bbb"));
        assertEquals(null, bean.get("ccc"));

        assertEquals(true, csvReader.hasNext());
        csvReader.read(bean);
        logger.debug(bean.toString());
        assertEquals("666", bean.get("aaa"));
        assertEquals("777", bean.get("bbb"));
        assertEquals("888", bean.get("ccc"));

        assertEquals(false, csvReader.hasNext());
        csvReader.close();
    }

}
