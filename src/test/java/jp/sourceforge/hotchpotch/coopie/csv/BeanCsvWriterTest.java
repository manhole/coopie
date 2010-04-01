package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.Closable;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.AaaBean;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.BeanColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvReaderTest.CsvSetting;

import org.junit.Test;
import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.ReaderUtil;
import org.t2framework.commons.util.ResourceUtil;

import au.com.bytecode.opencsv.CSVWriter;

public class BeanCsvWriterTest {

    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final BeanCsvWriter<AaaBean> csvWriter = new BeanCsvWriter<AaaBean>(
            AaaBean.class);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        csvWriter.open(writer);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc("う1");
        csvWriter.write(bean);

        bean.setAaa("あ2");
        bean.setBbb("い2");
        bean.setCcc("う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvWriterTest.class.getName() + "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
            "UTF-8"));
        assertEquals(expected, actual);
    }

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##

        final ColumnLayout layout = new ColumnLayout();
        layout.setNames(new String[] { "aaa", "ccc", "bbb" });

        final BeanCsvWriter<AaaBean> csvWriter = new BeanCsvWriter<AaaBean>(
            AaaBean.class, layout);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        csvWriter.open(writer);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc("う1");
        csvWriter.write(bean);

        bean.setAaa("あ2");
        bean.setBbb("い2");
        bean.setCcc("う2");
        csvWriter.write(bean);

        bean.setAaa("あ3");
        bean.setBbb("い3");
        bean.setCcc("う3");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
            "UTF-8"));
        assertEquals(expected, actual);
    }

    /**
     * CSVヘッダがBeanのプロパティ名と異なる場合。
     */
    @Test
    public void write3() throws Throwable {
        // ## Arrange ##
        final ColumnLayout layout = new ColumnLayout();
        //layout.setNames(new String[] { "aaa", "ccc", "bbb" });
        layout.addAlias("あ", "aaa");
        layout.addAlias("ううう", "ccc");
        layout.addAlias("いい", "bbb");

        final BeanCsvWriter<AaaBean> csvWriter = new BeanCsvWriter<AaaBean>(
            AaaBean.class, layout);

        // ## Act ##
        final StringWriter writer = new StringWriter();
        csvWriter.open(writer);

        final AaaBean bean = new AaaBean();
        bean.setAaa("あ1");
        bean.setBbb("い1");
        bean.setCcc("う1");
        csvWriter.write(bean);

        bean.setAaa("あ2");
        bean.setBbb("い2");
        bean.setCcc("う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        final String actual = writer.toString();

        final InputStream is = ResourceUtil.getResourceAsStream(
            BeanCsvReaderTest.class.getName() + "-2", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
            "UTF-8"));
        assertEquals(expected, actual);
    }

    public static class ColumnLayout<T> {

        private BeanColumnDesc<T>[] columnDescs;
        // 一時的
        private List<ColumnName> columnNames;

        private BeanColumnDesc<T>[] getColumnDescs() {
            if (columnDescs == null) {
                if (columnNames == null) {
                    return null;
                }
                columnDescs = new BeanColumnDesc[columnNames.size()];
                int i = 0;
                for (final ColumnName columnName : columnNames) {
                    final BeanColumnDesc<T> cd = new BeanColumnDesc();
                    cd.setName(columnName);
                    columnDescs[i] = cd;
                    i++;
                }
            }
            return columnDescs;
        }

        public void setup(final BeanDesc<T> beanDesc) {
            final BeanColumnDesc<T>[] cds = getColumnDescs();
            if (cds == null) {
                final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
                columnDescs = new BeanColumnDesc[pds.size()];
                int i = 0;
                for (final PropertyDesc<T> pd : pds) {
                    final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                    cd.setPropertyDesc(pd);
                    cd.setName(new SimpleColumnName(pd.getPropertyName()));
                    columnDescs[i] = cd;
                    i++;
                }
            } else {
                for (int i = 0; i < cds.length; i++) {
                    final BeanColumnDesc<T> cd = cds[i];
                    final String name = cd.getName().getName();
                    final PropertyDesc<T> pd = getPropertyDesc(beanDesc, name);
                    cd.setPropertyDesc(pd);
                }
                columnDescs = cds;
            }
        }

        public ColumnName[] getNames() {
            final BeanColumnDesc[] cds = getColumnDescs();
            final ColumnName[] names = new ColumnName[cds.length];
            for (int i = 0; i < cds.length; i++) {
                final BeanColumnDesc cd = cds[i];
                names[i] = cd.getName();
            }
            return names;
        }

        public void setNames(final String[] names) {
            for (int i = 0; i < names.length; i++) {
                final String name = names[i];
                final ColumnName columnName = new SimpleColumnName(name);
                addColumnName(columnName);
            }
        }

        public BeanColumnDesc getColumnDescByAlias(final String alias) {
            for (final BeanColumnDesc cd : columnDescs) {
                if (cd.getName().equals(alias)) {
                    return cd;
                }
            }
            // TODO 例外クラスにする
            throw new RuntimeException();
        }

        public void addAlias(final String alias, final String propertyName) {
            final SimpleColumnName columnName = new SimpleColumnName();
            columnName.setLabel(alias);
            columnName.setName(propertyName);
            addColumnName(columnName);
        }

        private void addColumnName(final ColumnName columnName) {
            if (columnNames == null) {
                columnNames = CollectionsUtil.newArrayList();
            }
            columnNames.add(columnName);
        }

        public String[] getValues(final T bean) {
            final BeanColumnDesc<T>[] cds = getColumnDescs();
            final String[] line = new String[cds.length];
            int i = 0;
            for (int j = 0; j < cds.length; j++) {
                final BeanColumnDesc cd = cds[j];
                final String v = cd.getValue(bean);
                line[i] = v;
                i++;
            }
            return line;
        }

        public void setValues(final T bean, final String[] line) {
            for (int i = 0; i < line.length; i++) {
                final String elem = line[i];
                final BeanColumnDesc<T> cd = columnDescs[i];
                cd.setValue(bean, elem);
            }
        }

        public void setupColumnDescByHeader(final BeanDesc<T> beanDesc,
            final String[] header) {

            if (getColumnDescs() == null) {
                /*
                 * CSVヘッダ名をbeanのプロパティ名として扱う。
                 */
                final BeanColumnDesc<T>[] cds = new BeanColumnDesc[header.length];
                for (int i = 0; i < header.length; i++) {
                    final String headerElem = header[i];
                    final PropertyDesc<T> pd = getPropertyDesc(beanDesc,
                        headerElem);
                    final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                    final ColumnName columnName = new SimpleColumnName(pd
                        .getPropertyName());
                    cd.setName(columnName);
                    cd.setPropertyDesc(pd);
                    cds[i] = cd;
                }
                columnDescs = cds;
            } else {
                /*
                 * 既にColumnDescが設定されている場合は、
                 * ヘッダの順序に合わせてソートし直す。
                 * 
                 * CSVヘッダ名を別名として扱う。
                 */
                final BeanColumnDesc<T>[] tmpCds = getColumnDescs();
                final BeanColumnDesc<T>[] cds = new BeanColumnDesc[tmpCds.length];

                int i = 0;
                HEADER: for (final String headerElem : header) {
                    for (final BeanColumnDesc<T> cd : tmpCds) {
                        final ColumnName name = cd.getName();
                        if (name.getLabel().equals(headerElem)) {
                            final PropertyDesc<T> pd = getPropertyDesc(
                                beanDesc, name.getName());
                            cd.setPropertyDesc(pd);

                            cds[i] = cd;
                            i++;
                            continue HEADER;
                        }
                    }
                    // TODO
                    throw new RuntimeException("headerElem=" + headerElem);
                }
                columnDescs = cds;
            }
        }

        private PropertyDesc<T> getPropertyDesc(final BeanDesc<T> beanDesc,
            final String name) {
            final PropertyDesc<T> pd = beanDesc.getPropertyDesc(name);
            if (pd == null) {
                // TODO
                throw new RuntimeException(name);
            }
            return pd;
        }
    }

    public static class BeanCsvWriter<T> implements Closable {

        private CsvSetting csvSetting = new CsvSetting();

        public CsvSetting getCsvSetting() {
            return csvSetting;
        }

        public void setCsvSetting(final CsvSetting csvSetting) {
            this.csvSetting = csvSetting;
        }

        private final BeanDesc<T> beanDesc;
        protected boolean closed = true;
        /**
         * BeanCsvWriter close時に、Writerを一緒にcloseする場合はtrue。
         */
        private boolean closeWriter = true;
        private CSVWriter csvWriter;
        private final ColumnLayout columnLayout;

        @SuppressWarnings("unchecked")
        public BeanCsvWriter(final Class<T> beanClass) {
            beanDesc = BeanDescFactory.getBeanDesc(beanClass);
            columnLayout = new ColumnLayout<T>();
            columnLayout.setup(beanDesc);
        }

        @SuppressWarnings("unchecked")
        public BeanCsvWriter(final Class<T> beanClass,
            final ColumnLayout columnLayout) {
            beanDesc = BeanDescFactory.getBeanDesc(beanClass);
            this.columnLayout = columnLayout;
            columnLayout.setup(beanDesc);
        }

        public void open(final Writer writer) {
            csvWriter = new CSVWriter(writer, csvSetting.getElementSeparator(),
                csvSetting.getQuoteMark(), csvSetting.getLineSeparator());
            writeHeader();
            closed = false;
        }

        private void writeHeader() {
            final ColumnName[] names = columnLayout.getNames();
            final String[] line = new String[names.length];
            int i = 0;
            for (final ColumnName name : names) {
                line[i] = name.getLabel();
                i++;
            }
            csvWriter.writeNext(line);
        }

        public void write(final T bean) {
            final String[] line = columnLayout.getValues(bean);
            csvWriter.writeNext(line);
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if (closeWriter) {
                csvWriter.close();
                csvWriter = null;
            }
        }

        public void setCloseWriter(final boolean closeWriter) {
            this.closeWriter = closeWriter;
        }

    }

}
