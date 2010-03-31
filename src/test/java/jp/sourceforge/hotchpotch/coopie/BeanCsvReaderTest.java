package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.ResourceUtil;

import au.com.bytecode.opencsv.CSVReader;

/*
 * CSVの設定としては、
 * - JavaBeanクラス
 * - 改行文字(writerのみ)
 * - 区切り文字: tabかカンマか
 * - CSVヘッダの有無
 * - CSV項目の順序
 *   ヘッダがある場合は、read時は不要。
 * - ""のときに、nullにするか、""にするか。
 */
public class BeanCsvReaderTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void read1() throws Throwable {
        // ## Arrange ##
        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-1", "tsv");

        final BeanCsvReader<AaaBean> csvReader = new BeanCsvReader<AaaBean>(
            AaaBean.class);

        // ## Act ##
        csvReader.open(new InputStreamReader(is, "UTF-8"));
        //logger.debug(ReaderUtil.readText(new InputStreamReader(is, "UTF-8")));

        final AaaBean bean = new AaaBean();
        csvReader.fetch(bean);

        // ## Assert ##
        logger.debug(bean.toString());
        assertEquals("あ1", bean.getAaa());
        assertEquals("い1", bean.getBbb());
        assertEquals("う1", bean.getCcc());

        csvReader.fetch(bean);
        logger.debug(bean.toString());
        assertEquals("あ2", bean.getAaa());
        assertEquals("い2", bean.getBbb());
        assertEquals("う2", bean.getCcc());

        csvReader.fetch(bean);
        logger.debug(bean.toString());
        assertEquals("あ3", bean.getAaa());
        assertEquals("い3", bean.getBbb());
        assertEquals("う3", bean.getCcc());

        csvReader.close();
    }

    public static class CsvSetting {

        public static final char TAB = '\t';
        public static final char COMMA = ',';
        public static final char DOUBLE_QUOTE = '\"';
        public static final String CRLF = "\r\n";

        /**
         * 要素区切り文字。
         * 未指定の場合はタブです。
         */
        private char elementSeparator = TAB;
        /**
         * 要素をクォートする文字。
         * 未指定の場合はダブルクォート(二重引用符)です。
         */
        private char quoteMark = DOUBLE_QUOTE;

        /**
         * 改行文字。
         * 未指定の場合はCRLFです。
         * 
         * CsvWriterを使う場合は、何らかの値が設定されている必要があります。
         * (CRLFのままでもOKです)
         * 
         * CsvReaderを使う場合は、未設定のままで構いません。
         */
        private String lineSeparator = CRLF;

        public String getLineSeparator() {
            return lineSeparator;
        }

        public void setLineSeparator(final String lineSeparator) {
            this.lineSeparator = lineSeparator;
        }

        public char getQuoteMark() {
            return quoteMark;
        }

        public void setQuoteMark(final char quoteMark) {
            this.quoteMark = quoteMark;
        }

        public char getElementSeparator() {
            return elementSeparator;
        }

        public void setElementSeparator(final char elementSeparator) {
            this.elementSeparator = elementSeparator;
        }

    }

    static class BeanColumnDesc<T> {

        /**
         * CSV列名。
         */
        private String name;

        /**
         * CSV列番号。
         * 
         * 左端を0とする、0始まり。
         */
        private int index;

        private PropertyDesc<T> propertyDesc;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(final int index) {
            this.index = index;
        }

        public PropertyDesc<T> getPropertyDesc() {
            return propertyDesc;
        }

        public void setPropertyDesc(final PropertyDesc<T> propertyDesc) {
            this.propertyDesc = propertyDesc;
        }

        public void setValue(final T bean, final String value) {
            propertyDesc.setValue(bean, value);
        }

    }

    public static class BeanCsvReader<T> implements Closable {

        private CsvSetting csvSetting = new CsvSetting();

        public CsvSetting getCsvSetting() {
            return csvSetting;
        }

        public void setCsvSetting(final CsvSetting csvSetting) {
            this.csvSetting = csvSetting;
        }

        /**
         * BeanCsvReader close時に、Readerを一緒にcloseする場合はtrue。
         */
        private boolean closeReader = true;
        private Boolean hasNext = null;
        private CSVReader csvReader;
        protected boolean closed = true;
        private final BeanDesc<T> beanDesc;
        private String[] nextLine;
        private BeanColumnDesc<T>[] columnDescs;

        public void fetch(final T bean) {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            hasNext = null;

            final String[] line;
            if (nextLine != null) {
                line = nextLine;
            } else {
                throw new AssertionError();
            }
            for (int i = 0; i < line.length; i++) {
                final String elem = line[i];
                final BeanColumnDesc<T> cd = columnDescs[i];
                cd.setValue(bean, elem);
            }
        }

        public void open(final Reader reader) {
            csvReader = new CSVReader(reader, csvSetting.getElementSeparator(),
                csvSetting.getQuoteMark());
            closed = false;

            setupColumnDescByHeader();
        }

        private void setupColumnDescByHeader() {
            final String[] header = readLine();

            columnDescs = new BeanColumnDesc[header.length];
            for (int i = 0; i < header.length; i++) {
                final PropertyDesc<T> pd = beanDesc.getPropertyDesc(header[i]);
                final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                cd.setIndex(i);
                cd.setName(pd.getPropertyName());
                cd.setPropertyDesc(pd);
                columnDescs[i] = cd;
            }
        }

        private String[] readLine() {
            try {
                return csvReader.readNext();
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

        private boolean hasNext() {
            if (hasNext != null) {
                return hasNext.booleanValue();
            }
            nextLine = readLine();

            if (nextLine == null) {
                hasNext = Boolean.FALSE;
            } else {
                hasNext = Boolean.TRUE;
            }
            return hasNext.booleanValue();
        }

        public BeanCsvReader(final Class<T> beanClass) {
            beanDesc = BeanDescFactory.getBeanDesc(beanClass);
        }

        public void setCloseReader(final boolean closeReader) {
            this.closeReader = closeReader;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (closeReader) {
                csvReader.close();
                csvReader = null;
            }
        }

    }

    public static class AaaBean {

        private String aaa;
        private String bbb;
        private String ccc;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        public String getBbb() {
            return bbb;
        }

        public void setBbb(final String bbb) {
            this.bbb = bbb;
        }

        public String getCcc() {
            return ccc;
        }

        public void setCcc(final String ccc) {
            this.ccc = ccc;
        }

        private final ToStringFormat toStringFormat = new ToStringFormat();

        @Override
        public String toString() {
            return toStringFormat.format(this);
        }

    }

}
