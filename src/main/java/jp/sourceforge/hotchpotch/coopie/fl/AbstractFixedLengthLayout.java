package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.IOException;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.InternalColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.SimpleColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnName;
import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup;
import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup.ColumnBuilder;
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
import org.t2framework.commons.util.StringUtil;

abstract class AbstractFixedLengthLayout<T> {

    // 固定長ファイルでは「ヘッダ無し」をデフォルトにする。
    private boolean withHeader_ = false;
    private RecordDesc<T> recordDesc_;
    private FixedLengthRecordDef recordDef_;

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
        recordDef_ = setup.getRecordDef();
    }

    protected RecordDesc<T> getRecordDesc() {
        return recordDesc_;
    }

    protected void setRecordDesc(final RecordDesc<T> recordDesc) {
        recordDesc_ = recordDesc;
    }

    protected FixedLengthRecordDef getRecordDef() {
        return recordDef_;
    }

    protected void setRecordDef(final FixedLengthRecordDef recordDef) {
        recordDef_ = recordDef;
    }

    protected boolean isWithHeader() {
        return withHeader_;
    }

    public void setWithHeader(final boolean withHeader) {
        withHeader_ = withHeader;
    }

    protected LineReaderHandler getLineReaderHandler() {
        return lineReaderHandler_;
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

    protected ElementReaderHandler getElementReaderHandler() {
        return elementReaderHandler_;
    }

    public void setElementReaderHandler(
            final ElementReaderHandler elementReaderHandler) {
        elementReaderHandler_ = elementReaderHandler;
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

    protected void setFixedLengthElementDescs(
            final FixedLengthElementDesc[] fixedLengthElementDescs) {
        fixedLengthElementDescs_ = fixedLengthElementDescs;
    }

    /**
     * カスタマイズ用hander実装をまとめて登録する、コンビニエンスメソッドです。
     * 
     * @param handler {@link LineReaderHandler} {@link ElementReaderHandler}
     *  {@link ElementEditor} の1つ以上をimplementsしたインスタンス
     * @exception IllegalArgumentException 上記インタフェースを1つもimplementsしていない場合
     * 
     * @see #setLineReaderHandler(LineReaderHandler)
     * @see #setElementReaderHandler(ElementReaderHandler)
     * @see #setElementEditor(ElementEditor)
     */
    public void setReaderHandler(final Object handler) {
        int assigned = 0;
        if (handler instanceof LineReaderHandler) {
            setLineReaderHandler(LineReaderHandler.class.cast(handler));
            assigned++;
        }
        if (handler instanceof ElementReaderHandler) {
            setElementReaderHandler(ElementReaderHandler.class.cast(handler));
            assigned++;
        }
        if (handler instanceof ElementEditor) {
            setElementEditor(ElementEditor.class.cast(handler));
            assigned++;
        }

        /*
         * いずれもsetできない場合は例外にする。
         */
        if (assigned == 0) {
            throw new IllegalArgumentException("no suitable");
        }
    }

    protected ElementInOut createElementInOut() {
        final FixedLengthElementInOut a = new FixedLengthElementInOut(
                getFixedLengthElementDescs());
        a.setLineReaderHandler(getLineReaderHandler());
        return a;
    }

    protected static interface FixedLengthRecordDescSetup extends
            FixedLengthColumnSetup {

        FixedLengthRecordDef getRecordDef();

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

        private final List<FlColumnBuilder> columnBuilders_ = CollectionsUtil
                .newArrayList();
        private FixedLengthRecordDef recordDef_;

        @Override
        public CsvColumnSetup.ColumnBuilder column(final String name,
                final int beginIndex, final int endIndex) {

            final FixedLengthColumnDef def = c(name, beginIndex, endIndex);
            return column(def);
        }

        @Override
        public ColumnBuilder column(final FixedLengthColumnDef columnDef) {
            final FlColumnBuilder builder = new FlColumnBuilder();
            builder.addFixedLengthColumnDef(columnDef);
            builder.toProperty(columnDef.getPropertyName());
            columnBuilders_.add(builder);
            return builder;
        }

        @Override
        public ColumnBuilder columns(final FixedLengthColumnDef... columnDefs) {
            final FlColumnBuilder builder = new FlColumnBuilder();
            for (final FixedLengthColumnDef columnDef : columnDefs) {
                builder.addFixedLengthColumnDef(columnDef);
            }
            columnBuilders_.add(builder);
            return builder;
        }

        @Override
        public FixedLengthColumnDef c(final String name, final int beginIndex,
                final int endIndex) {

            final DefaultFixedLengthColumnDef def = new DefaultFixedLengthColumnDef();
            def.setPropertyName(name);
            def.setBeginIndex(beginIndex);
            def.setEndIndex(endIndex);
            return def;
        }

        @Override
        public FixedLengthRecordDef getRecordDef() {
            buildIfNeed();
            return recordDef_;
        }

        private void buildIfNeed() {
            if (recordDef_ != null) {
                return;
            }

            /*
             * 設定されているプロパティ名を対象に。
             */
            final DefaultFixedLengthRecordDef recordDef = new DefaultFixedLengthRecordDef();
            for (final InternalFlColumnBuilder builder : columnBuilders_) {
                final List<FixedLengthColumnDef> columnDefs = builder
                        .getColumnDefs();
                if (1 < columnDefs.size()) {
                    final FixedLengthColumnsDef columnsDef = toColumnsDef(builder);
                    recordDef.addColumnsDef(columnsDef);
                } else {
                    final FixedLengthColumnDef columnDef = toColumnDef(builder);
                    recordDef.addColumnDef(columnDef);
                }
            }
            recordDef_ = recordDef;
        }

        private FixedLengthColumnDef toColumnDef(
                final InternalFlColumnBuilder builder) {
            final List<FixedLengthColumnDef> columnDefs = builder
                    .getColumnDefs();
            if (columnDefs.size() != 1) {
                throw new IllegalStateException();
            }

            final FixedLengthColumnDef columnDef = columnDefs.get(0);
            columnDef.setConverter(builder.getConverter());
            return columnDef;
        }

        private FixedLengthColumnsDef toColumnsDef(
                final InternalFlColumnBuilder builder) {
            final List<FixedLengthColumnDef> columnDefs = builder
                    .getColumnDefs();
            if (columnDefs.size() < 2) {
                throw new IllegalStateException();
            }
            final DefaultFixedLengthColumnsDef sdef = new DefaultFixedLengthColumnsDef();
            {
                final String n = builder.getPropertyName();
                if (StringUtil.isEmpty(n)) {
                    // TODO
                    //                    throw new IllegalStateException(
                    //                            "property is not specified. for column {"
                    //                                    + columnNames + "}");
                    throw new IllegalStateException();
                }
                sdef.setPropertyName(n);
            }
            for (final FixedLengthColumnDef columnDef : columnDefs) {
                sdef.addColumnDef(columnDef);
            }
            sdef.setConverter(builder.getConverter());
            return sdef;
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

        private final RecordDesc<T> delegate_;

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
            // setupByBeanが先に動作するため、ここは通らない
            throw new UnsupportedOperationException();
        }

        @Override
        public RecordDesc<T> setupByHeader(final String[] header) {
            // 固定長では、ヘッダがあっても見ない
            return this;
        }

    }

    public interface InternalFlColumnBuilder extends InternalColumnBuilder {

        List<FixedLengthColumnDef> getColumnDefs();

    }

    static class FlColumnBuilder extends SimpleColumnBuilder implements
            InternalFlColumnBuilder {

        private final List<FixedLengthColumnDef> columnDefs_ = CollectionsUtil
                .newArrayList();

        public void addFixedLengthColumnDef(final FixedLengthColumnDef columnDef) {
            columnDefs_.add(columnDef);
            // TODO この時点ではColumnNameを作らなくて良い。
            // 後からも作るため、
            final ColumnName columnName = new SimpleColumnName(
                    columnDef.getPropertyName());
            addColumnName(columnName);

        }

        @Override
        public List<FixedLengthColumnDef> getColumnDefs() {
            return columnDefs_;
        }

    }

}
