package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;
import java.util.List;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractFixedLengthLayout<T> {

    // 固定長ファイルでは「ヘッダ無し」をデフォルトにする。
    private boolean withHeader_ = false;
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

    protected static class SimpleFixedLengthColumnDesc implements
            FixedLengthColumnDesc {

        private final int beginIndex_;
        private final int endIndex_;
        private final int length_;

        SimpleFixedLengthColumnDesc(final int beginIndex, final int endIndex) {
            beginIndex_ = beginIndex;
            endIndex_ = endIndex;
            length_ = endIndex - beginIndex;
        }

        public int getBeginIndex() {
            return beginIndex_;
        }

        public int getEndIndex() {
            return endIndex_;
        }

        @Override
        public String read(final CharSequence line) {
            final int len = length(line);
            final int begin = Math.min(len, beginIndex_);
            final int end = Math.min(len, endIndex_);
            final CharSequence s = line.subSequence(begin, end);
            final String trimmed = s.toString().trim();
            return trimmed;
        }

        @Override
        public void write(final CharSequence elem, final Appendable appendable) {
            final CharSequence padded = lpad(elem, length_, ' ');
            try {
                appendable.append(padded);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

        private CharSequence lpad(final CharSequence str, final int len,
                final char pad) {
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

        private int length(final CharSequence str) {
            if (str == null) {
                return 0;
            }
            return str.length();
        }

    }

    protected abstract static class AbstractFixedLengthRecordDescSetup<T>
            implements FixedLengthRecordDescSetup {

        private final List<NameAndDesc> columns_ = CollectionsUtil
                .newArrayList();

        @Override
        public void column(final String propertyName, final int beginIndex,
                final int endIndex) {
            final ColumnName columnName = new SimpleColumnName(propertyName);
            final SimpleFixedLengthColumnDesc c = new SimpleFixedLengthColumnDesc(
                    beginIndex, endIndex);
            final NameAndDesc nd = new NameAndDesc(columnName, c);
            columns_.add(nd);
        }

        private FixedLengthRecordDesc<T> fixedLengthRecordDesc_;
        private FixedLengthElementSetting fixedLengthElementSetting_;

        @Override
        public RecordDesc<T> getRecordDesc() {
            buildIfNeed();
            return fixedLengthRecordDesc_;
        }

        @Override
        public ElementSetting getElementSetting() {
            buildIfNeed();
            return fixedLengthElementSetting_;
        }

        private void buildIfNeed() {
            if (fixedLengthRecordDesc_ != null) {
                return;
            }

            /*
             * 設定されているプロパティ名を対象に。
             */
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            final FixedLengthColumnDesc[] flColumnDescs = new FixedLengthColumnDesc[columns_
                    .size()];
            int i = 0;
            for (final NameAndDesc nd : columns_) {
                final ColumnName cn = nd.columnName_;
                columnNames.add(cn);
                final FixedLengthColumnDesc desc = nd.fixedLengthColumnDesc_;
                flColumnDescs[i] = desc;
                i++;
            }

            final ColumnDesc<T>[] cds = createColumnDescs(columnNames);
            fixedLengthRecordDesc_ = new FixedLengthRecordDesc<T>(cds,
                    getRecordType());
            fixedLengthElementSetting_ = new FixedLengthElementSetting(
                    flColumnDescs);
        }

        abstract protected ColumnDesc<T>[] createColumnDescs(
                List<ColumnName> columnNames);

        abstract protected RecordType<T> getRecordType();

    }

    private static class NameAndDesc {

        final ColumnName columnName_;
        final FixedLengthColumnDesc fixedLengthColumnDesc_;

        public NameAndDesc(final ColumnName columnName,
                final SimpleFixedLengthColumnDesc desc) {
            columnName_ = columnName;
            fixedLengthColumnDesc_ = desc;
        }

    }

    protected static class FixedLengthElementSetting implements ElementSetting {

        private final FixedLengthColumnDesc[] columns_;

        protected FixedLengthElementSetting(
                final FixedLengthColumnDesc[] columns) {
            columns_ = columns;
        }

        @Override
        public ElementWriter openWriter(final Appendable appendable) {
            return new FixedLengthWriter(appendable, columns_);
        }

        @Override
        public ElementReader openReader(final Readable readable) {
            return new FixedLengthReader(readable, columns_);
        }

    }

    protected static class FixedLengthRecordDesc<T> implements RecordDesc<T> {

        private final DefaultRecordDesc<T> delegate_;

        protected FixedLengthRecordDesc(final ColumnDesc<T>[] columnDescs,
                final RecordType<T> recordType) {
            // 固定長なので、常に指定した順序
            delegate_ = new DefaultRecordDesc<T>(columnDescs,
                    OrderSpecified.SPECIFIED, recordType);
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

    }

}
