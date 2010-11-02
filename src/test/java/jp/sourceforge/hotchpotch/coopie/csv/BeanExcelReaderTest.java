package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.ResourceUtil;

public class BeanExcelReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * CSVヘッダがBeanのプロパティ名と同じ場合。
     * 
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
                BeanCsvReaderTest.class.getName() + "-1", "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        final AaaBean bean = new AaaBean();

        // ## Assert ##
        BeanCsvReaderTest.assertRead1(csvReader, bean);
    }
}
