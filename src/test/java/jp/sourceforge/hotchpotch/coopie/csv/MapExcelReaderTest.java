package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ResourceUtil;

public class MapExcelReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * ヘッダがBeanのプロパティ名と同じ場合。
     * 
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-1", "xls");

        final MapExcelLayout layout = new MapExcelLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead1(csvReader, bean);
    }

    /**
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-2", "xls");

        final MapExcelLayout layout = new MapExcelLayout();
        layout.setupColumns(new ColumnSetupBlock() {
            @Override
            public void setup(final ColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
                setup.column("bbb", "いい");
            }
        });

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead2(csvReader, bean);
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
                BeanCsvReaderTest.class.getName() + "-4", "xls");

        final MapExcelLayout layout = new MapExcelLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertRead3(csvReader, bean);
    }

    /**
     * recordインスタンスをCsvReaderに生成させる。
     */
    @Test
    public void read3_2() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-4", "xls");

        final MapExcelLayout layout = new MapExcelLayout();

        // ## Act ##
        final CsvReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        MapCsvReaderTest.assertRead3_2(csvReader);
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-3", "xls");

        final MapExcelLayout layout = new MapExcelLayout();
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
        final CsvReader<Map<String, String>> csvReader = layout.openReader(is);

        // ## Assert ##
        final Map<String, String> bean = CollectionsUtil.newHashMap();
        MapCsvReaderTest.assertReadNoheader(csvReader, bean);
    }

}
