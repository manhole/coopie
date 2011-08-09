package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;

import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.TestLayout;

import org.junit.Test;
import org.slf4j.Logger;

public class BeanExcelReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    /**
     * ヘッダがBeanのプロパティ名と同じ場合。
     * 
     * Layoutを未設定のまま。
     */
    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead1(csvReader, bean);
    }

    /**
     * ヘッダがBeanのプロパティ名と異なる場合。
     * ヘッダ名とbeanのプロパティ名をマッピングすること。
     */
    @Test
    public void read2() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("bbb", "いい");
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead2(csvReader, bean);
    }

    /**
     * 空白項目がある場合。
     * 
     * ""はnullとして扱い、" "は" "として扱う。
     */
    @Test
    public void read3() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-4",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead3(csvReader, bean);
    }

    /**
     * ヘッダが無い場合。
     */
    @Test
    public void read_noheader() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-3",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * CSVの列順
                 */
                setup.column("ccc", "ううう");
                setup.column("aaa", "あ");
                setup.column("bbb", "いい");
            }
        });

        layout.setWithHeader(false);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadNoheader(csvReader, bean);
    }

    /**
     * 空行がある場合。
     * 
     * 各要素を"" (null)として扱う。
     */
    @Test
    public void read_empty_row() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-5",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadEmptyRow(csvReader, bean);
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read4() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-2",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa", "あ");
                setup.column("ccc", "ううう");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead4(csvReader, bean);
    }

    /**
     * setupしない列が入力ファイルに存在する場合は無視する。
     */
    @Test
    public void read5() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-6",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc", "ddd");
            }
        });

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertRead5(csvReader, bean);
    }

    @Test
    public void read_customLayout() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-7",
                "xls");

        final BeanExcelLayout<AaaBean> layout = new BeanExcelLayout<AaaBean>(
                AaaBean.class);
        layout.setCustomLayout(new TestLayout());

        // ## Act ##
        final CsvReader<AaaBean> csvReader = layout.openReader(is);

        // ## Assert ##
        final AaaBean bean = new AaaBean();
        BeanCsvReaderTest.assertReadCustomLayout(csvReader, bean);
    }

}
