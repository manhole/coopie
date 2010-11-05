package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.csv.CsvReader;
import jp.sourceforge.hotchpotch.coopie.csv.MapCsvLayout;
import junitx.framework.ListAssert;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ResourceUtil;

public class ExcelToCsvTest {

    private static final Logger logger = LoggerFactory.getLogger();
    private final FileOperation files = new FileOperation();

    @Test
    public void test1() throws Exception {
        // ## Arrange ##
        final File inFile = ResourceUtil.getResourceAsFile(Sha1Test.class
                .getPackage().getName().replace('.', '/')
                + "/ExcelToCsvTest-1.xls");
        final File outFile = new File(inFile.getParentFile(),
                "ExcelToCsvTest-1.tsv");
        logger.debug("outFile={}", outFile);

        files.delete(outFile);

        // ## Act ##
        final ExcelToCsv excelToCsv = new ExcelToCsv();
        excelToCsv.writeTsv(inFile);

        // ## Assert ##
        assertEquals(true, outFile.exists());

        final File expectedFile = ResourceUtil.getResourceAsFile(Sha1Test.class
                .getPackage().getName().replace('.', '/')
                + "/" + "ExcelToCsvTest-1-expected.xls.tsv");

        assertCsvEquals(expectedFile, outFile);
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
