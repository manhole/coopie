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

package jp.sourceforge.hotchpotch.coopie.spreadsheet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.File;

import jp.sourceforge.hotchpotch.coopie.csv.CsvAssert;
import jp.sourceforge.hotchpotch.coopie.csv.QuoteMode;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;
import jp.sourceforge.hotchpotch.coopie.util.FileOperation;
import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;
import jp.sourceforge.hotchpotch.coopie.util.LineSeparator;
import jp.sourceforge.hotchpotch.coopie.util.ResourceUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

public class ExcelToCsvTest {

    private static final Logger logger = LoggerFactory.getLogger();
    private final FileOperation files_ = new FileOperation();
    private File rootDir_;
    private final CsvAssert csvAssert_ = new CsvAssert();

    @Before
    public void setUp() throws Throwable {
        rootDir_ = files_.createTempDir();
    }

    @After
    public void tearDown() {
        files_.delete(rootDir_);
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
        final File inFile = new File(rootDir_, testFile.getName());
        files_.copy(testFile, inFile);

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
        final File inFile = new File(rootDir_, testFile.getName());
        files_.copy(testFile, inFile);

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

        final File inFile = new File(rootDir_, testFile.getName());
        files_.copy(testFile, inFile);

        final File outFile1 = new File(rootDir_, "ExcelToCsvTest-3-Sheet1.tsv");
        final File outFile2 = new File(rootDir_, "ExcelToCsvTest-3-Sheet2.tsv");
        final File outFile3 = new File(rootDir_, "ExcelToCsvTest-3-Sheet3.tsv");
        logger.debug("outFile={}", outFile1);
        assertEquals(1, rootDir_.list().length);

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile1.exists());
        assertEquals("空シートはcsvに出力しない", false, outFile2.exists());
        assertEquals(true, outFile3.exists());
        assertEquals(3, rootDir_.list().length);

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
        final File inFile = new File(rootDir_, testFile.getName());
        files_.copy(testFile, inFile);

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

    /**
     * 未指定の場合は、要素に必ずクォートが付く
     */
    @Test
    public void quoteMode_default() throws Throwable {
        // ## Arrange ##
        final File excelFile = new File(rootDir_, "quoteMode_default.xls");
        assertThat(excelFile.exists(), is(false));
        {
            final DefaultExcelWriter.PoiWriter writer = new DefaultExcelWriter.PoiWriter(
                    files_.openBufferedOutputStream(excelFile));
            writer.open();
            writer.writeRecord(new String[] { "a", "\"b", "c" });
            writer.writeRecord(new String[] { "1", "2", "3\n4" });
            writer.close();
        }

        final File outFile = new File(rootDir_, "quoteMode_default.tsv");
        assertThat(outFile.exists(), is(false));

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(excelFile);

        // ## Assert ##
        assertThat(outFile.exists(), is(true));
        logger.debug("[{}]", files_.read(outFile));

        final LineReadable lineReader = files_.openLineReader(outFile);
        {
            final Line line = lineReader.readLine();
            assertThat(line.getSeparator(), is(LineSeparator.CRLF));
            assertThat(line.getBody(), is("\"a\"\t\"\"\"b\"\t\"c\""));
        }
        {
            final Line line = lineReader.readLine();
            assertThat(line.getSeparator(), is(LineSeparator.LF));
            assertThat(line.getBody(), is("\"1\"\t\"2\"\t\"3"));
        }
        {
            final Line line = lineReader.readLine();
            assertThat(line.getSeparator(), is(LineSeparator.CRLF));
            assertThat(line.getBody(), is("4\""));
        }

        assertThat(lineReader.readLine(), is(nullValue()));
    }

    /**
     * QuoteMode.MINIMUM に変更できること。
     */
    @Test
    public void quoteMode_minimum() throws Throwable {
        // ## Arrange ##
        final File excelFile = new File(rootDir_, "quoteMode_minimum.xls");
        assertThat(excelFile.exists(), is(false));
        {
            final DefaultExcelWriter.PoiWriter writer = new DefaultExcelWriter.PoiWriter(
                    files_.openBufferedOutputStream(excelFile));
            writer.open();
            writer.writeRecord(new String[] { "a", "\"b", "c" });
            writer.writeRecord(new String[] { "1", "2", "3\n4" });
            writer.close();
        }

        final File outFile = new File(rootDir_, "quoteMode_minimum.tsv");
        assertThat(outFile.exists(), is(false));

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.getOrCreateCsvSetting().setQuoteMode(QuoteMode.MINIMUM);
        excelToCsv.writeTsv(excelFile);

        // ## Assert ##
        assertThat(outFile.exists(), is(true));
        logger.debug("[{}]", files_.read(outFile));

        final LineReadable lineReader = files_.openLineReader(outFile);
        {
            final Line line = lineReader.readLine();
            assertThat(line.getSeparator(), is(LineSeparator.CRLF));
            assertThat(line.getBody(), is("a\t\"\"\"b\"\tc"));
        }
        {
            final Line line = lineReader.readLine();
            assertThat(line.getSeparator(), is(LineSeparator.LF));
            assertThat(line.getBody(), is("1\t2\t\"3"));
        }
        {
            final Line line = lineReader.readLine();
            assertThat(line.getSeparator(), is(LineSeparator.CRLF));
            assertThat(line.getBody(), is("4\""));
        }

        assertThat(lineReader.readLine(), is(nullValue()));
    }

    private File getResourceAsFile(final String nameSuffix, final String ext) {
        final File resource = ResourceUtil.getResourceAsFile(ExcelToCsvTest.class.getName() + nameSuffix, ext);
        assertThat(resource, is(notNullValue()));
        return resource;
    }

}
