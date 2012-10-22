package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.AbstractColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.InternalColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnDescs;
import jp.sourceforge.hotchpotch.coopie.csv.ColumnName;
import jp.sourceforge.hotchpotch.coopie.csv.CompositeColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.Converter;
import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup;
import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup.ColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultColumnDesc;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultElementReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultLineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.ElementEditor;
import jp.sourceforge.hotchpotch.coopie.csv.ElementInOut;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReader;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.ElementWriter;
import jp.sourceforge.hotchpotch.coopie.csv.LineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBinding;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBindingFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordType;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;
import jp.sourceforge.hotchpotch.coopie.csv.SimpleColumnName;
import jp.sourceforge.hotchpotch.coopie.util.Text;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

abstract class AbstractFixedLengthLayout<BEAN> {

    // 固定長ファイルでは「ヘッダ無し」をデフォルトにする。
    private boolean withHeader_ = false;
    private RecordDesc<BEAN> recordDesc_;
    private FixedLengthRecordDef recordDef_;

    private ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler
            .getInstance();
    private LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();
    private ElementEditor elementEditor_;
    private FixedLengthElementDesc[] fixedLengthElementDescs_;

    protected FixedLengthRecordDefSetup getRecordDefSetup() {
        return new DefaultFixedLengthRecordDefSetup();
    }

    public void setupColumns(final SetupBlock<FixedLengthColumnSetup> block) {
        final FixedLengthRecordDefSetup setup = getRecordDefSetup();
        block.setup(setup);
        recordDef_ = setup.getRecordDef();
    }

    protected RecordDesc<BEAN> getRecordDesc() {
        return recordDesc_;
    }

    protected void setRecordDesc(final RecordDesc<BEAN> recordDesc) {
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

    protected ColumnDesc<BEAN>[] recordDefToColumnDesc(
            final FixedLengthRecordDef recordDef,
            final PropertyBindingFactory<BEAN> pbf) {

        final List<ColumnDesc<BEAN>> list = CollectionsUtil.newArrayList();
        appendColumnDescFromColumnDef(recordDef, list, pbf);
        appendColumnDescFromColumnsDef(recordDef, list, pbf);
        final ColumnDesc<BEAN>[] cds = ColumnDescs.newColumnDescs(list.size());
        list.toArray(cds);
        return cds;
    }

    private void appendColumnDescFromColumnDef(
            final FixedLengthRecordDef recordDef,
            final List<ColumnDesc<BEAN>> list,
            final PropertyBindingFactory<BEAN> pbf) {

        for (final FixedLengthColumnDef columnDef : recordDef.getColumnDefs()) {
            final ColumnName columnName = newColumnName(columnDef);
            final PropertyBinding<BEAN, Object> pb = pbf
                    .getPropertyBinding(columnDef.getPropertyName());
            final ColumnDesc<BEAN> cd = DefaultColumnDesc.newColumnDesc(
                    columnName, pb, columnDef.getConverter());
            list.add(cd);
        }
    }

    private void appendColumnDescFromColumnsDef(
            final FixedLengthRecordDef recordDef,
            final List<ColumnDesc<BEAN>> list,
            final PropertyBindingFactory<BEAN> pbf) {

        for (final FixedLengthColumnsDef columnsDef : recordDef
                .getColumnsDefs()) {
            final List<ColumnName> columnNames = CollectionsUtil.newArrayList();
            for (final FixedLengthColumnDef columnDef : columnsDef
                    .getColumnDefs()) {
                columnNames.add(newColumnName(columnDef));
            }
            final PropertyBinding<BEAN, Object> pb = pbf
                    .getPropertyBinding(columnsDef.getPropertyName());
            final ColumnDesc<BEAN>[] cds = CompositeColumnDesc
                    .newCompositeColumnDesc(columnNames, pb,
                            columnsDef.getConverter());
            Collections.addAll(list, cds);
        }
    }

    private ColumnName newColumnName(final FixedLengthColumnDef columnDef) {
        final SimpleColumnName columnName = new SimpleColumnName(
                columnDef.getPropertyName());
        return columnName;
    }

    protected FixedLengthElementDesc[] recordDefToElementDescs(
            final FixedLengthRecordDef recordDef) {
        final List<FixedLengthElementDesc> elementDescs = CollectionsUtil
                .newArrayList();
        for (final FixedLengthColumnDef columnDef : recordDef
                .getAllColumnDefs()) {
            final FixedLengthElementDesc elementDesc = new SimpleFixedLengthElementDesc(
                    columnDef.getBeginIndex(), columnDef.getEndIndex());
            elementDescs.add(elementDesc);
        }

        final FixedLengthElementDesc[] descs = new FixedLengthElementDesc[elementDescs
                .size()];
        elementDescs.toArray(descs);
        return descs;
    }

    protected static interface FixedLengthRecordDefSetup extends
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

    protected static class DefaultFixedLengthRecordDefSetup implements
            FixedLengthRecordDefSetup {

        private final List<InternalFixedLengthColumnBuilder> columnBuilders_ = CollectionsUtil
                .newArrayList();
        private FixedLengthRecordDef recordDef_;

        @Override
        public CsvColumnSetup.ColumnBuilder column(final String name,
                final int beginIndex, final int endIndex) {

            final FixedLengthColumnDef def = c(name, beginIndex, endIndex);
            final ColumnBuilder builder = builder(def);
            return builder;
        }

        @Override
        public ColumnBuilder column(final FixedLengthColumnDef columnDef) {
            final FixedLengthColumnBuilder builder = builder(columnDef);
            return builder;
        }

        private FixedLengthColumnBuilder builder(
                final FixedLengthColumnDef columnDef) {
            final FixedLengthColumnBuilder builder = new FixedLengthColumnBuilder(
                    columnDef);
            columnBuilders_.add(builder);
            return builder;
        }

        @Override
        public ColumnBuilder columns(final FixedLengthColumnDef... columnDefs) {
            final DefaultFixedLengthColumnsDef columnsDef = new DefaultFixedLengthColumnsDef();
            for (final FixedLengthColumnDef columnDef : columnDefs) {
                columnsDef.addColumnDef(columnDef);
            }
            final FixedLengthCompositeColumnBuilder builder = new FixedLengthCompositeColumnBuilder(
                    columnsDef);
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
            for (final InternalFixedLengthColumnBuilder builder : columnBuilders_) {
                if (builder.isMultipleColumns()) {
                    final FixedLengthColumnsDef columnsDef = builder
                            .getCompositeColumnDef();
                    recordDef.addColumnsDef(columnsDef);
                } else {
                    final FixedLengthColumnDef columnDef = builder
                            .getColumnDef();
                    recordDef.addColumnDef(columnDef);
                }
            }
            recordDef_ = recordDef;
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

    protected static class FixedLengthRecordDesc<BEAN> implements
            RecordDesc<BEAN> {

        private final RecordDesc<BEAN> delegate_;

        protected FixedLengthRecordDesc(final ColumnDesc<BEAN>[] columnDescs,
                final RecordType<BEAN> recordType) {
            // 固定長なので、常に指定した順序
            delegate_ = new DefaultRecordDesc<BEAN>(columnDescs,
                    OrderSpecified.SPECIFIED, recordType);
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            return delegate_.getOrderSpecified();
        }

        @Override
        public String[] getValues(final BEAN bean) {
            return delegate_.getValues(bean);
        }

        @Override
        public void setValues(final BEAN bean, final String[] values) {
            delegate_.setValues(bean, values);
        }

        @Override
        public RecordDesc<BEAN> setupByBean(final BEAN bean) {
            return delegate_.setupByBean(bean);
        }

        @Override
        public BEAN newInstance() {
            return delegate_.newInstance();
        }

        @Override
        public String[] getHeaderValues() {
            // setupByBeanが先に動作するため、ここは通らない
            throw new UnsupportedOperationException();
        }

        @Override
        public RecordDesc<BEAN> setupByHeader(final String[] header) {
            // 固定長では、ヘッダがあっても見ない
            return this;
        }

    }

    public interface InternalFixedLengthColumnBuilder extends
            InternalColumnBuilder {

        FixedLengthColumnDef getColumnDef();

        FixedLengthColumnsDef getCompositeColumnDef();

    }

    static class FixedLengthColumnBuilder extends AbstractColumnBuilder
            implements InternalFixedLengthColumnBuilder {

        private final FixedLengthColumnDef columnDef_;

        public FixedLengthColumnBuilder(final FixedLengthColumnDef columnDef) {
            columnDef_ = columnDef;
        }

        @Override
        public boolean isMultipleColumns() {
            return false;
        }

        @Override
        public FixedLengthColumnDef getColumnDef() {
            return columnDef_;
        }

        @Override
        public FixedLengthColumnsDef getCompositeColumnDef() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ColumnBuilder toProperty(final String propertyName) {
            columnDef_.setPropertyName(propertyName);
            return this;
        }

        @Override
        public void withConverter(final Converter converter) {
            columnDef_.setConverter(converter);
        }

    }

    static class FixedLengthCompositeColumnBuilder extends
            AbstractColumnBuilder implements InternalFixedLengthColumnBuilder {

        private final FixedLengthColumnsDef columnsDef_;

        public FixedLengthCompositeColumnBuilder(
                final FixedLengthColumnsDef columnsDef) {
            columnsDef_ = columnsDef;
        }

        @Override
        public boolean isMultipleColumns() {
            return true;
        }

        @Override
        public FixedLengthColumnDef getColumnDef() {
            throw new UnsupportedOperationException();
        }

        @Override
        public FixedLengthColumnsDef getCompositeColumnDef() {
            return columnsDef_;
        }

        @Override
        public ColumnBuilder toProperty(final String propertyName) {
            columnsDef_.setPropertyName(propertyName);
            return this;
        }

        @Override
        public void withConverter(final Converter converter) {
            columnsDef_.setConverter(converter);
        }

    }

}
