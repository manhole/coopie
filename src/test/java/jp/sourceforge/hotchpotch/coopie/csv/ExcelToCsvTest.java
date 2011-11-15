package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.File;

import jp.sourceforge.hotchpotch.coopie.FileOperation;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.ResourceUtil;

public class ExcelToCsvTest {

    private static final Logger logger = LoggerFactory.getLogger();
    private final FileOperation files = new FileOperation();
    private File rootDir;
    private final CsvAssert csvAssert_ = new CsvAssert();

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
        csvAssert_.assertCsvEquals(expectedFile, outFile);
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

        csvAssert_.assertCsvEquals(expectedFile, outFile);
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

        csvAssert_.assertCsvEquals(expectedFile1, outFile1);
    }

}
