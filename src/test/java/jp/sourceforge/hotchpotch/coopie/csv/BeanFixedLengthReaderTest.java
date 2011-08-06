package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.InputStreamReader;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.ResourceUtil;

public class BeanFixedLengthReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = getResourceAsStream("-1", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new FixedLengthColumnSetupBlock() {
            @Override
            public void setup(final FixedLengthColumnSetup columnSetup) {
                columnSetup.column("aaa", 0, 5);
                columnSetup.column("ccc", 5, 12);
                columnSetup.column("bbb", 12, 20);
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead1(csvReader, bean);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanFixedLengthReaderTest.class.getName() + suffix, ext);
    }

}
