package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractFixedLengthLayout<T> extends AbstractLayout<T> {

    private final BeanDesc<T> beanDesc;
    protected boolean withHeader = true;
    protected FixedLengthColumn[] columns;

    public AbstractFixedLengthLayout(final Class<T> beanClass) {
        beanDesc = BeanDescFactory.getBeanDesc(beanClass);
    }

    public void setupColumns(final FixedLengthColumnSetupBlock block) {
        final DefaultFixedLengthColumnSetup columnSetup = new DefaultFixedLengthColumnSetup();
        block.setup(columnSetup);
        final FixedLengthColumn[] columns = columnSetup.toColumns();
        this.columns = columns;
    }

    protected FixedLengthRecordDesc<T> buildRecordDesc() {
        /*
         * 設定されているプロパティ名を対象に。
         */
        final ColumnDesc<T>[] cds = newColumnDescs(columns.length);
        int i = 0;
        for (final FixedLengthColumn column : columns) {
            final String propertyName = column.getName();
            final PropertyDesc<T> pd = AbstractBeanCsvLayout.getPropertyDesc(
                    beanDesc, propertyName);
            final ColumnDesc<T> cd = AbstractBeanCsvLayout.newBeanColumnDesc(
                    column, pd);
            cds[i] = cd;
            i++;
        }

        return new FixedLengthRecordDesc<T>(cds,
                new BeanRecordType<T>(beanDesc), columns);
    }

    public void setWithHeader(final boolean withHeader) {
        this.withHeader = withHeader;
    }

    protected static class SimpleFixedLengthColumn implements FixedLengthColumn {

        private final String propertyName;
        private final int beginIndex;
        private final int endIndex;

        SimpleFixedLengthColumn(final String propertyName,
                final int beginIndex, final int endIndex) {
            this.propertyName = propertyName;
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }

        @Override
        public String getName() {
            return propertyName;
        }

        public int getBeginIndex() {
            return beginIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        @Override
        public String getLabel() {
            // nameと同じで良いでしょう
            return getName();
        }

        @Override
        public String read(final String line) {
            final int len = line.length();
            final int begin = Math.min(len, beginIndex);
            final int end = Math.min(len, endIndex);
            final String s = line.substring(begin, end);
            final String trimmed = s.trim();
            return trimmed;
        }

    }

    private static class DefaultFixedLengthColumnSetup implements
            FixedLengthColumnSetup {

        final List<FixedLengthColumn> columns = CollectionsUtil.newArrayList();

        public FixedLengthColumn[] toColumns() {
            return columns.toArray(new FixedLengthColumn[columns.size()]);
        }

        @Override
        public void column(final String propertyName, final int beginIndex,
                final int endIndex) {
            final SimpleFixedLengthColumn c = new SimpleFixedLengthColumn(
                    propertyName, beginIndex, endIndex);
            columns.add(c);
        }

    }

    protected static class FixedLengthRecordDesc<T> implements RecordDesc<T>,
            ElementSetting {

        protected ColumnDesc<T>[] columnDescs;
        private final DefaultRecordDesc<T> delegate;
        private final FixedLengthColumn[] columns;

        protected FixedLengthRecordDesc(final ColumnDesc<T>[] columnDescs,
                final RecordType<T> recordType,
                final FixedLengthColumn[] columns) {
            // 固定長なので、常に指定した順序
            delegate = new DefaultRecordDesc<T>(columnDescs,
                    OrderSpecified.SPECIFIED, recordType);
            this.columns = columns;
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            return delegate.getOrderSpecified();
        }

        @Override
        public String[] getValues(final T bean) {
            return delegate.getValues(bean);
        }

        @Override
        public void setValues(final T bean, final String[] values) {
            delegate.setValues(bean, values);
        }

        @Override
        public RecordDesc<T> setupByBean(final T bean) {
            return delegate.setupByBean(bean);
        }

        @Override
        public T newInstance() {
            return delegate.newInstance();
        }

        @Override
        public String[] getHeaderValues() {
            // 固定長だからヘッダは無い
            return null;
        }

        @Override
        public RecordDesc<T> setupByHeader(final String[] header) {
            // 固定長では、ヘッダがあっても見ない
            return this;
        }

        @Override
        public CsvElementWriter openWriter(final Writer writer) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public CsvElementReader openReader(final Reader reader) {
            return new FixedLengthReader(reader, columns);
        }

    }

}