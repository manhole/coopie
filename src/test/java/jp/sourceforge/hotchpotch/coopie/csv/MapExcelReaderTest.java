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

}
