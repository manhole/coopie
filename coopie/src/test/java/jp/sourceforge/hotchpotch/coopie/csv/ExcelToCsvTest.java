/*
 * Copyright 2010 manhole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.csv;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.FileOperation;

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
    public void test1_xls() throws Exception {
        _test1("xls");
    }

    @Test
    public void test1_xlsx() throws Exception {
        _test1("xlsx");
    }

    private void _test1(final String ext) throws Exception {
        // ## Arrange ##
        final File testFile = getResourceAsFile("-1", ext);
        final File inFile = new File(rootDir, testFile.getName());
        files.copy(testFile, inFile);

        final File outFile = new File(inFile.getParentFile(), "ExcelToCsvTest-1.tsv");
        logger.debug("outFile={}", outFile);
        assertThat(outFile.exists(), is(false));

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile.exists());

        final File expectedFile = getResourceAsFile("-1-expected", "tsv");
        csvAssert_.assertCsvEquals(expectedFile, outFile);
    }

    /**
     * 空行を含む場合
     */
    @Test
    public void test2_xls() throws Exception {
        _test2("xls");
    }

    @Test
    public void test2_xlsx() throws Exception {
        _test2("xlsx");
    }

    private void _test2(final String ext) throws Exception {
        // ## Arrange ##
        final File testFile = getResourceAsFile("-2", ext);
        final File inFile = new File(rootDir, testFile.getName());
        files.copy(testFile, inFile);

        final File outFile = new File(inFile.getParentFile(), "ExcelToCsvTest-2.tsv");
        logger.debug("outFile={}", outFile);
        assertThat(outFile.exists(), is(false));

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile.exists());

        final File expectedFile = getResourceAsFile("-2-expected", "tsv");

        csvAssert_.assertCsvEquals(expectedFile, outFile);
    }

    /**
     * 複数シートを持つ場合
     */
    @Test
    public void test3_xls() throws Exception {
        _test3("xls");
    }

    @Test
    public void test3_xlsx() throws Exception {
        _test3("xlsx");
    }

    private void _test3(final String ext) throws Exception {
        // ## Arrange ##
        final File testFile = getResourceAsFile("-3", ext);

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
        assertEquals("空シートはcsvに出力しない", false, outFile2.exists());
        assertEquals(true, outFile3.exists());
        assertEquals(3, rootDir.list().length);

        final File expectedFile1 = getResourceAsFile("-3-expected1", "tsv");
        final File expectedFile3 = getResourceAsFile("-3-expected3", "tsv");

        csvAssert_.assertCsvEquals(expectedFile1, outFile1);
        csvAssert_.assertCsvEquals(expectedFile3, outFile3);
    }

    @Test
    public void test_date_xls() throws Exception {
        _test_date("xls");
    }

    @Test
    public void test_date_xlsx() throws Exception {
        _test_date("xlsx");
    }

    private void _test_date(final String ext) throws Exception {
        // ## Arrange ##
        final File testFile = getResourceAsFile("-date", ext);
        final File inFile = new File(rootDir, testFile.getName());
        files.copy(testFile, inFile);

        final File outFile = new File(inFile.getParentFile(), "ExcelToCsvTest-date.tsv");
        logger.debug("outFile={}", outFile);
        assertThat(outFile.exists(), is(false));

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile.exists());

        final File expectedFile = getResourceAsFile("-date-expected", "tsv");
        csvAssert_.assertCsvEquals(expectedFile, outFile);
    }

    private File getResourceAsFile(final String nameSuffix, final String ext) {
        return ResourceUtil.getResourceAsFile(ExcelToCsvTest.class.getName() + nameSuffix, ext);
    }

}
