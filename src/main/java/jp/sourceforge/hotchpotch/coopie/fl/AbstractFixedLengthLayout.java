package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.IOException;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnName;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultElementReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultLineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.ElementEditor;
import jp.sourceforge.hotchpotch.coopie.csv.ElementInOut;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReader;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.ElementWriter;
import jp.sourceforge.hotchpotch.coopie.csv.LineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordType;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.csv.SimpleColumnName;
import jp.sourceforge.hotchpotch.coopie.util.Text;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractFixedLengthLayout<T> {

    // 固定長ファイルでは「ヘッダ無し」をデフォルトにする。
    private boolean withHeader_ = false;
    private RecordDesc<T> recordDesc_;
    private ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler
            .getInstance();
    private LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();
    private ElementEditor elementEditor_;
    private FixedLengthElementDesc[] fixedLengthElementDescs_;

    protected abstract FixedLengthRecordDescSetup getRecordDescSetup();

    public void setupColumns(final SetupBlock<FixedLengthColumnSetup> block) {
        final FixedLengthRecordDescSetup setup = getRecordDescSetup();
        block.setup(setup);
        recordDesc_ = setup.getRecordDesc();
        fixedLengthElementDescs_ = setup.getElementDescs();
    }

    protected RecordDesc<T> getRecordDesc() {
        return recordDesc_;
    }

    protected void setRecordDesc(final RecordDesc<T> recordDesc) {
        recordDesc_ = recordDesc;
    }

    protected boolean isWithHeader() {
        return withHeader_;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected ElementReaderHandler getElementReaderHandler() {
        return elementReaderHandler_;
    }

    public void setElementReaderHandler(
            final ElementReaderHandler elementReaderHandler) {
        elementReaderHandler_ = elementReaderHandler;
    }

    protected LineReaderHandler getLineReaderHandler() {
        return lineReaderHandler_;
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

    protected ElementEditor getElementEditor() {
        return elementEditor_;
    }

    public void setElementEditor(final ElementEditor elementEditor) {
        elementEditor_ = elementEditor;
    }

    protected FixedLengthElementDesc[] getFixedLengthElementDescs() {
        return fixedLengthElementDescs_;
    }

    protected ElementInOut createElementInOut() {
        final FixedLengthElementInOut a = new FixedLengthElementInOut(
                getFixedLengthElementDescs());
        a.setLineReaderHandler(getLineReaderHandler());
        return a;
    }

    protected static interface FixedLengthRecordDescSetup extends
            FixedLengthColumnSetup {

        <T> RecordDesc<T> getRecordDesc();

        FixedLengthElementDesc[] getElementDescs();

    }

    protected static class SimpleFixedLengthElementDesc implements
            FixedLengthElementDesc {

        private final int beginIndex_;
        private final int endIndex_;
        private final int length_;

        SimpleFixedLengthElementDesc(final int beginIndex, final int endIndex) {
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
            final String str = line.toString();
            final int len = Text.length(str);
            final int begin = Math.min(len, beginIndex_);
            final int end = Math.min(len, endIndex_);
            final String s = Text.substring(str, begin, end);
            final String trimmed = s.trim();
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

        private CharSequence lpad(final CharSequence elem, final int len,
                final char pad) {
            final int strlen = Text.length(elem);
            final int padlen = len - strlen;
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < padlen; i++) {
                sb.append(pad);
            }
            if (elem != null) {
                sb.append(elem);
            }
            return sb.toString();
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
            final SimpleFixedLengthElementDesc c = new SimpleFixedLengthElementDesc(
                    beginIndex, endIndex);
            final NameAndDesc nd = new NameAndDesc(columnName, c);
            columns_.add(nd);
        }

        private FixedLengthRecordDesc<T> fixedLengthRecordDesc_;
        private FixedLengthElementDesc[] fixedLengthElementDescs_;

        @Override
        public RecordDesc<T> getRecordDesc() {
            buildIfNeed();
            return fixedLengthRecordDesc_;
        }

        @Override
        public FixedLengthElementDesc[] getElementDescs() {
            return fixedLengthElementDescs_;
        }

        private void buildIfNeed() {
            if (fixedLengthRecordDesc_ != null) {
                return;
            }

            /*
             * 設定されているプロパティ名を対象に。
             */
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            final FixedLengthElementDesc[] flColumnDescs = new FixedLengthElementDesc[columns_
                    .size()];
            int i = 0;
            for (final NameAndDesc nd : columns_) {
                final ColumnName cn = nd.columnName_;
                columnNames.add(cn);
                final FixedLengthElementDesc desc = nd.fixedLengthElementDesc_;
                flColumnDescs[i] = desc;
                i++;
            }

            final ColumnDesc<T>[] cds = createColumnDescs(columnNames);
            fixedLengthRecordDesc_ = new FixedLengthRecordDesc<T>(cds,
                    getRecordType());
            fixedLengthElementDescs_ = flColumnDescs;
        }

        abstract protected ColumnDesc<T>[] createColumnDescs(
                List<ColumnName> columnNames);

        abstract protected RecordType<T> getRecordType();

    }

    private static class NameAndDesc {

        final ColumnName columnName_;
        final FixedLengthElementDesc fixedLengthElementDesc_;

        public NameAndDesc(final ColumnName columnName,
                final SimpleFixedLengthElementDesc desc) {
            columnName_ = columnName;
            fixedLengthElementDesc_ = desc;
        }

    }

    protected static class FixedLengthElementInOut implements ElementInOut {

        private final FixedLengthElementDesc[] elementDescs_;
        private LineReaderHandler lineReaderHandler_;

        protected FixedLengthElementInOut(
                final FixedLengthElementDesc[] elementDescs) {
            elementDescs_ = elementDescs;
        }

        @Override
        public ElementWriter openWriter(final Appendable appendable) {
            final FixedLengthWriter writer = createWriter(appendable);
            writer.open(appendable);
            return writer;
        }

        @Override
        public ElementReader openReader(final Readable readable) {
            final FixedLengthReader reader = createReader();
            reader.open(readable);
            return reader;
        }

        protected FixedLengthWriter createWriter(final Appendable appendable) {
            final FixedLengthWriter writer = new FixedLengthWriter(
                    elementDescs_);
            return writer;
        }

        protected FixedLengthReader createReader() {
            final FixedLengthReader reader = new FixedLengthReader(
                    elementDescs_);
            if (lineReaderHandler_ != null) {
                reader.setLineReaderHandler(lineReaderHandler_);
            }
            return reader;
        }

        public void setLineReaderHandler(
                final LineReaderHandler lineReaderHandler) {
            lineReaderHandler_ = lineReaderHandler;
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
