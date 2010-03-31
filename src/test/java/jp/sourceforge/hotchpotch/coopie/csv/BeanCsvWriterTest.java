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

    public static class ColumnLayout<T> {

        private BeanColumnDesc<T>[] columnDescs;
        private List<ColumnName> columnNames;

        private BeanColumnDesc[] getColumnDescs() {
            if (columnDescs == null) {
                columnDescs = new BeanColumnDesc[columnNames.size()];
                int i = 0;
                for (final ColumnName columnName : columnNames) {
                    final BeanColumnDesc columnDesc = new BeanColumnDesc();
                    columnDesc.setName(columnName);
                    columnDescs[i] = columnDesc;
                    i++;
                }
            }
            return columnDescs;
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
                final ColumnName columnName = new ColumnName(name);
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
            final ColumnName columnName = new ColumnName();
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

        public void setValues(final T bean, final String[] line) {
            for (int i = 0; i < line.length; i++) {
                final String elem = line[i];
                final BeanColumnDesc<T> cd = columnDescs[i];
                cd.setValue(bean, elem);
            }
        }

        public void adjust(final BeanDesc<T> beanDesc) {
            final BeanColumnDesc<T>[] cds = getColumnDescs();
            for (final BeanColumnDesc<T> cd : cds) {
                final String propertyName = cd.getName().getName();
                final PropertyDesc<T> pd = beanDesc
                    .getPropertyDesc(propertyName);
                if (pd == null) {
                    // TODO 例外
                    throw new RuntimeException();
                }
                cd.setPropertyDesc(pd);
            }
        }

        public void setupColumnDescByHeader(final BeanDesc<T> beanDesc,
            final String[] header) {

            if (columnDescs == null) {
                final BeanColumnDesc<T>[] cds = new BeanColumnDesc[header.length];
                for (int i = 0; i < header.length; i++) {
                    final String headerElem = header[i];
                    final PropertyDesc<T> pd = beanDesc
                        .getPropertyDesc(headerElem);
                    final BeanColumnDesc<T> cd = new BeanColumnDesc<T>();
                    final ColumnName columnName = new ColumnName();
                    columnName.setLabel(pd.getPropertyName());
                    columnName.setName(pd.getPropertyName());
                    cd.setName(columnName);
                    cd.setPropertyDesc(pd);
                    cds[i] = cd;
                }
                columnDescs = cds;
            } else {
                /*
                 * 既にColumnDescが設定されている場合は、
                 * ヘッダの順序に合わせてソートし直す。
                 */
                final BeanColumnDesc<T>[] tmpCds = getColumnDescs();
                final BeanColumnDesc<T>[] cds = new BeanColumnDesc[tmpCds.length];

                int i = 0;
                HEADER: for (final String headerElem : header) {
                    for (final BeanColumnDesc<T> cd : tmpCds) {
                        if (cd.getName().getLabel().equals(headerElem)) {
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

    }

    public static class ColumnName {

        public ColumnName() {
        }

        public ColumnName(final String labelAndName) {
            setLabel(labelAndName);
            setName(labelAndName);
        }

        /**
         * CSVの項目名
         */
        private String label;

        public String getLabel() {
            return label;
        }

        public void setLabel(final String label) {
            this.label = label;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Beanのプロパティ名
         */
        private String name;

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
        private final PropertyDesc<T>[] propertyDescs;

        @SuppressWarnings("unchecked")
        public BeanCsvWriter(final Class<T> beanClass) {
            beanDesc = BeanDescFactory.getBeanDesc(beanClass);
            final List<PropertyDesc<T>> pds = beanDesc.getAllPropertyDesc();
            propertyDescs = pds.toArray(new PropertyDesc[pds.size()]);
        }

        @SuppressWarnings("unchecked")
        public BeanCsvWriter(final Class<T> beanClass,
            final ColumnLayout columnLayout) {
            beanDesc = BeanDescFactory.getBeanDesc(beanClass);
            final ColumnName[] names = columnLayout.getNames();
            propertyDescs = new PropertyDesc[names.length];
            for (int i = 0; i < names.length; i++) {
                final String name = names[i].getName();
                final PropertyDesc<T> pd = beanDesc.getPropertyDesc(name);
                propertyDescs[i] = pd;
            }
        }

        public void open(final Writer writer) {
            csvWriter = new CSVWriter(writer, csvSetting.getElementSeparator(),
                csvSetting.getQuoteMark(), csvSetting.getLineSeparator());
            writeHeader();
            closed = false;
        }

        private void writeHeader() {
            final String[] line = new String[propertyDescs.length];
            int i = 0;
            for (final PropertyDesc<T> pd : propertyDescs) {
                final String v = pd.getPropertyName();
                line[i] = v;
                i++;
            }

            csvWriter.writeNext(line);

        }

        public void write(final T bean) {
            final String[] line = new String[propertyDescs.length];
            int i = 0;
            for (final PropertyDesc<T> pd : propertyDescs) {
                final Object v = pd.getValue(bean);
                line[i] = v != null ? String.valueOf(v) : null;
                i++;
            }
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
