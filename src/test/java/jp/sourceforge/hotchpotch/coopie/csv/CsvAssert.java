package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.File;
import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.FileOperation;
import jp.sourceforge.hotchpotch.coopie.IOUtil;
import junitx.framework.ListAssert;

import org.t2framework.commons.util.CollectionsUtil;

public class CsvAssert {

    private final FileOperation files_ = new FileOperation();

    public void assertCsvEquals(final File expectedFile, final File actualFile) {
        final List<Map<String, String>> actList;
        {
            final MapCsvLayout layout = new MapCsvLayout();
            layout.setWithHeader(true);
            final CsvReader<Map<String, String>> actualCsvReader = layout
                    .openReader(files_.openBufferedReader(actualFile));
            actList = readAll(actualCsvReader);
        }

        final List<Map<String, String>> exList;
        {
            final MapCsvLayout layout = new MapCsvLayout();
            layout.setWithHeader(true);

            final CsvReader<Map<String, String>> expectedCsvReader = layout
                    .openReader(files_.openBufferedReader(expectedFile));
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
