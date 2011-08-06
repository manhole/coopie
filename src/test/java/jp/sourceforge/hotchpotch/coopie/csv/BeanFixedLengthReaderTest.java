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

    /**
     * ファイルヘッダがBeanのプロパティ名と同じ場合。
     * 
     * ※固定長ファイルでは、ヘッダがあっても大事に扱わない。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = getResourceAsStream("-1", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new FixedLengthColumnSetupBlock() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
                setup.column("bbb", 12, 20);
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead1(csvReader, bean);
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
        final InputStream is = getResourceAsStream("-2", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new FixedLengthColumnSetupBlock() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("aaa", 0, 5);
                setup.column("ccc", 5, 12);
                setup.column("bbb", 12, 30);
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead2(csvReader, bean);
    }

    /**
     * ファイルヘッダが無い場合。
     * 
     * ※これが通常の固定長ファイル
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = getResourceAsStream("-3", "tsv");

        final BeanFixedLengthLayout<AaaBean> layout = new BeanFixedLengthLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new FixedLengthColumnSetupBlock() {
            @Override
            public void setup(final FixedLengthColumnSetup setup) {
                setup.column("ccc", 0, 6);
                setup.column("aaa", 6, 12);
                setup.column("bbb", 12, 20);
            }
        });

        layout.setWithHeader(false);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout
                .openReader(new InputStreamReader(is, "UTF-8"));

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadNoheader(csvReader, bean);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                BeanFixedLengthReaderTest.class.getName() + suffix, ext);
    }

}
