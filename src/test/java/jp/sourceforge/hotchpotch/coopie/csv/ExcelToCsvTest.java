package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.FileOperation;
import jp.sourceforge.hotchpotch.coopie.IOUtil;
import jp.sourceforge.hotchpotch.coopie.LoggerFactory;
import junitx.framework.ListAssert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ResourceUtil;

public class ExcelToCsvTest {

    private static final Logger logger = LoggerFactory.getLogger();
    private final FileOperation files = new FileOperation();
    private File rootDir;

    @Before
    public void setUp() throws Throwable {
        rootDir = files.createTempDir();
    }

    @After
    public void tearDown() {
        files.delete(rootDir);
    }

    @Test
    public void test1() throws Exception {
        // ## Arrange ##
        final File testFile = ResourceUtil.getResourceAsFile(
                ExcelToCsvTest.class.getName() + "-1", "xls");
        final File inFile = new File(rootDir, testFile.getName());
        files.copy(testFile, inFile);

        final File outFile = new File(inFile.getParentFile(),
                "ExcelToCsvTest-1.tsv");
        logger.debug("outFile={}", outFile);

        files.delete(outFile);

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile.exists());

        final File expectedFile = ResourceUtil.getResourceAsFile(
                ExcelToCsvTest.class.getName() + "-1-expected", "tsv");
        assertCsvEquals(expectedFile, outFile);
    }

    /**
     * 空行を含む場合
     */
    @Test
    public void test2() throws Exception {
        // ## Arrange ##
        final File testFile = ResourceUtil.getResourceAsFile(
                ExcelToCsvTest.class.getName() + "-2", "xls");
        final File inFile = new File(rootDir, testFile.getName());
        files.copy(testFile, inFile);

        final File outFile = new File(inFile.getParentFile(),
                "ExcelToCsvTest-2.tsv");
        logger.debug("outFile={}", outFile);

        files.delete(outFile);

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile.exists());

        final File expectedFile = ResourceUtil.getResourceAsFile(
                ExcelToCsvTest.class.getName() + "-2-expected", "tsv");

        assertCsvEquals(expectedFile, outFile);
    }

    /**
     * 複数シートを持つ場合
     */
    @Test
    public void test3() throws Exception {
        // ## Arrange ##
        final File testFile = ResourceUtil.getResourceAsFile(
                ExcelToCsvTest.class.getName() + "-3", "xls");

        final File inFile = new File(rootDir, testFile.getName());
        files.copy(testFile, inFile);

        final File outFile1 = new File(rootDir, "ExcelToCsvTest-3-Sheet1.tsv");
        final File outFile2 = new File(rootDir, "ExcelToCsvTest-3-Sheet2.tsv");
        final File outFile3 = new File(rootDir, "ExcelToCsvTest-3-Sheet3.tsv");
        logger.debug("outFile={}", outFile1);
        assertEquals(1, rootDir.list().length);

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile1.exists());
        assertEquals(false, outFile2.exists());
        assertEquals(true, outFile3.exists());
        assertEquals(3, rootDir.list().length);

        final File expectedFile1 = ResourceUtil
                .getResourceAsFile(ExcelToCsvTest.class.getPackage().getName()
                        .replace('.', '/')
                        + "/" + "ExcelToCsvTest-3-expected1.tsv");

        assertCsvEquals(expectedFile1, outFile1);
    }

    private void assertCsvEquals(final File expectedFile, final File actualFile) {
        final List<Map<String, String>> actList;
        {
            final MapCsvLayout layout = new MapCsvLayout();
            layout.setWithHeader(true);
            final CsvReader<Map<String, String>> actualCsvReader = layout
                    .openReader(files.openBufferedReader(actualFile));
            actList = readAll(actualCsvReader);
        }

        final List<Map<String, String>> exList;
        {
            final MapCsvLayout layout = new MapCsvLayout();
            layout.setWithHeader(true);

            final CsvReader<Map<String, String>> expectedCsvReader = layout
                    .openReader(files.openBufferedReader(expectedFile));
            exList = readAll(expectedCsvReader);
        }
        ListAssert.assertEquals(exList, actList);
    }

    private <T> List<T> readAll(final CsvReader<T> reader) {
        final List<T> list = CollectionsUtil.newArrayList();
        while (reader.hasNext()) {
            final T bean = reader.read();
            list.add(bean);
        }
        IOUtil.closeNoException(reader);
        return list;
    }

}
