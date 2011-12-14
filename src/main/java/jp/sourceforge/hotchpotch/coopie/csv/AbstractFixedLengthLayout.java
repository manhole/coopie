package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractFixedLengthLayout<T> {

    private boolean withHeader_ = true;
    private RecordDesc<T> recordDesc_;
    private ElementSetting elementSetting_;

    protected abstract FixedLengthRecordDescSetup getRecordDescSetup();

    public void setupColumns(final SetupBlock<FixedLengthColumnSetup> block) {
        final FixedLengthRecordDescSetup setup = getRecordDescSetup();
        block.setup(setup);
        recordDesc_ = setup.getRecordDesc();
        elementSetting_ = setup.getElementSetting();
    }

    protected RecordDesc<T> getRecordDesc() {
        return recordDesc_;
    }

    protected ElementSetting getElementSetting() {
        return elementSetting_;
    }

    protected boolean isWithHeader() {
        return withHeader_;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected static interface FixedLengthRecordDescSetup extends
            FixedLengthColumnSetup {

        <T> RecordDesc<T> getRecordDesc();

        ElementSetting getElementSetting();

    }

    protected static class SimpleFixedLengthColumn implements FixedLengthColumn {

        private final String propertyName_;
        private final int beginIndex_;
        private final int endIndex_;
        private final int length_;

        SimpleFixedLengthColumn(final String propertyName,
                final int beginIndex, final int endIndex) {
            propertyName_ = propertyName;
            beginIndex_ = beginIndex;
            endIndex_ = endIndex;
            length_ = endIndex - beginIndex;
        }

        @Override
        public String getName() {
            return propertyName_;
        }

        public int getBeginIndex() {
            return beginIndex_;
        }

        public int getEndIndex() {
            return endIndex_;
        }

        @Override
        public String getLabel() {
            // nameと同じで良いでしょう
            return getName();
        }

        @Override
        public boolean labelEquals(final String label) {
            return getLabel().equals(label);
        }

        @Override
        public String read(final String line) {
            final int len = length(line);
            final int begin = Math.min(len, beginIndex_);
            final int end = Math.min(len, endIndex_);
            final String s = line.substring(begin, end);
            final String trimmed = s.trim();
            return trimmed;
        }

        @Override
        public void write(final String s, final Appendable appendable) {
            final String elem = lpad(s, length_, ' ');
            try {
                appendable.append(elem);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

        private String lpad(final String str, final int len, final char pad) {
            final StringBuilder sb = new StringBuilder();
            final int padlen = len - length(str);
            for (int i = 0; i < padlen; i++) {
                sb.append(pad);
            }
            if (str != null) {
                sb.append(str);
            }
            return sb.toString();
        }

        private int length(final String str) {
            if (str == null) {
                return 0;
            }
            return str.length();
        }

    }

    protected abstract static class AbstractFixedLengthRecordDescSetup<T>
            implements FixedLengthRecordDescSetup {

        protected final List<FixedLengthColumn> columns_ = CollectionsUtil
                .newArrayList();

        @Override
        public void column(final String propertyName, final int beginIndex,
                final int endIndex) {
            final SimpleFixedLengthColumn c = new SimpleFixedLengthColumn(
                    propertyName, beginIndex, endIndex);
            columns_.add(c);
        }

    }

    protected static class FixedLengthRecordDesc<T> implements RecordDesc<T>,
            ElementSetting {

        private final DefaultRecordDesc<T> delegate_;
        private final FixedLengthColumn[] columns_;

        protected FixedLengthRecordDesc(final ColumnDesc<T>[] columnDescs,
                final RecordType<T> recordType,
                final FixedLengthColumn[] columns) {
            // 固定長なので、常に指定した順序
            delegate_ = new DefaultRecordDesc<T>(columnDescs,
                    OrderSpecified.SPECIFIED, recordType);
            columns_ = columns;
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            return delegate_.getOrderSpecified();
        }

        @Override
        public String[] getValues(final T bean) {
            return delegate_.getValues(bean);
        }

        @Override
        public void setValues(final T bean, final String[] values) {
            delegate_.setValues(bean, values);
        }

        @Override
        public RecordDesc<T> setupByBean(final T bean) {
            return delegate_.setupByBean(bean);
        }

        @Override
        public T newInstance() {
            return delegate_.newInstance();
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
        public ElementWriter openWriter(final Writer writer) {
            return new FixedLengthWriter(writer, columns_);
        }

        @Override
        public ElementReader openReader(final Reader reader) {
            return new FixedLengthReader(reader, columns_);
        }

    }

}
