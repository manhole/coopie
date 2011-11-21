package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.File;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.util.FileOperation;
import jp.sourceforge.hotchpotch.coopie.util.IOUtil;

public class CsvAssert {

    private final FileOperation files_ = new FileOperation();
    private char elementSeparator_ = CsvSetting.TAB;

    /**
     * CSVとして等しいかassertします。
     * 区切り文字やクォート有無の違いについては対象外です。
     */
    public void assertCsvEquals(final File expectedFile, final File actualFile) {
        final Reader actualReader = files_.openBufferedReader(actualFile);
        final Reader expectedReader = files_.openBufferedReader(expectedFile);
        assertCsvEquals(expectedReader, actualReader);
    }

    /**
     * CSVとして等しいかassertします。
     * 区切り文字やクォート有無の違いについては対象外です。
     */
    public void assertCsvEquals(final Reader expectedReader,
            final Reader actualReader) {
        if (nullarg(expectedReader, actualReader)) {
            return;
        }

        try {
            final RecordReader<Map<String, String>> actualCsvReader = openMapReader(actualReader);
            final RecordReader<Map<String, String>> expectedCsvReader = openMapReader(expectedReader);
            assertCsvEquals(expectedCsvReader, actualCsvReader);
        } finally {
            IOUtil.closeNoException(expectedReader);
            IOUtil.closeNoException(actualReader);
        }
    }

    private RecordReader<Map<String, String>> openMapReader(
            final Reader actualReader) {
        final MapCsvLayout layout = new MapCsvLayout();
        layout.setWithHeader(true);
        layout.setElementSeparator(elementSeparator_);
        final RecordReader<Map<String, String>> csvReader = layout
                .openReader(actualReader);
        return csvReader;
    }

    private void assertCsvEquals(
            final RecordReader<Map<String, String>> expectedCsvReader,
            final RecordReader<Map<String, String>> actualCsvReader) {

        while (expectedCsvReader.hasNext() && actualCsvReader.hasNext()) {
            final Map<String, String> exp = expectedCsvReader.read();
            final Map<String, String> act = actualCsvReader.read();
            if (!exp.equals(act)) {
                throw new CsvAssertionError("expected:<" + exp + "> but was:<"
                        + act + ">");
            }
        }
        if (expectedCsvReader.hasNext() || actualCsvReader.hasNext()) {
            throw new CsvAssertionError("different size");
        }

        IOUtil.closeNoException(expectedCsvReader);
        IOUtil.closeNoException(actualCsvReader);
    }

    public void assertArrayEquals(final String[] expected, final String[] actual) {
        if (nullarg(expected, actual)) {
            return;
        }
        if (expected.length != actual.length) {
            throw new CsvAssertionError("size is not equals. expected:<"
                    + expected.length + "> " + Arrays.toString(expected)
                    + ", actual:<" + actual.length + "> "
                    + Arrays.toString(actual));
        }
        for (int i = 0; i < expected.length; i++) {
            if (!expected[i].equals(actual[i])) {
                throw new CsvAssertionError("[" + i
                        + "] is not equals. expected:<" + expected[i]
                        + ">, actual:<" + actual[i] + ">");
            }
        }
    }

    private boolean nullarg(final Object expected, final Object actual) {
        if (expected == null && actual == null) {
            return true;
        }
        if (expected == null) {
            throw new CsvAssertionError("expected was null");
        }
        if (actual == null) {
            throw new CsvAssertionError("actual was null");
        }
        return false;
    }

    static class CsvAssertionError extends AssertionError {

        private static final long serialVersionUID = 1L;

        public CsvAssertionError(final String message) {
            super(message);
        }

    }

    public void setElementSeparator(final char elementSeparator) {
        elementSeparator_ = elementSeparator;
    }

}
