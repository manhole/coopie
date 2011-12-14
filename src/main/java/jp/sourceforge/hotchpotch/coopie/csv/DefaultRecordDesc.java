package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.util.CollectionsUtil;

public class DefaultRecordDesc<T> implements RecordDesc<T> {

    private static final Logger logger = LoggerFactory.getLogger();

    private ColumnDesc<T>[] columnDescs_;
    private final OrderSpecified orderSpecified_;
    private final RecordType<T> recordType_;
    private ColumnDesc<T>[] ignoredColumnDescs_ = ColumnDescs.newColumnDescs(0);

    public DefaultRecordDesc(final ColumnDesc<T>[] columnDescs,
            final OrderSpecified orderSpecified, final RecordType<T> recordType) {
        columnDescs_ = columnDescs;
        orderSpecified_ = orderSpecified;
        recordType_ = recordType;
    }

    protected ColumnDesc<T>[] getColumnDescs() {
        return columnDescs_;
    }

    @Override
    public String[] getHeaderValues() {
        final ColumnDesc<T>[] cds = getColumnDescs();
        if (cds == null) {
            return null;
        }
        final String[] line = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<T> cd = cds[i];
            final ColumnName cn = cd.getName();
            final String label = cn.getLabel();
            line[i] = label;
        }
        return line;
    }

    @Override
    public OrderSpecified getOrderSpecified() {
        return orderSpecified_;
    }

    @Override
    public String[] getValues(final T bean) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        final String[] values = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<T> cd = cds[i];
            final String value = cd.getValue(bean);
            values[i] = value(value);
        }
        return values;
    }

    @Override
    public void setValues(final T bean, final String[] values) {
        final ColumnDesc<T>[] cds = getColumnDescs();
        int i = 0;
        for (; i < values.length; i++) {
            final String value = value(values[i]);
            final ColumnDesc<T> cd = cds[i];
            cd.setValue(bean, value);
        }
        for (; i < cds.length; i++) {
            final String value = null;
            final ColumnDesc<T> cd = cds[i];
            cd.setValue(bean, value);
        }
        for (final ColumnDesc<T> cd : ignoredColumnDescs_) {
            cd.setValue(bean, null);
        }
    }

    /*
     * ""はnullと見なす。
     */
    private String value(final String v) {
        if (v == null || v.isEmpty()) {
            return null;
        }
        return v;
    }

    /*
     * CSVを読むとき
     */
    @Override
    public RecordDesc<T> setupByHeader(final String[] header) {
        logger.debug("setupByHeader: {}", Arrays.toString(header));
        /*
         * ColumnDescをヘッダの順序に合わせてソートし直す。
         */
        final List<ColumnDesc<T>> tmpCds = CollectionsUtil.newArrayList();
        Collections.addAll(tmpCds, getColumnDescs());
        final ColumnDesc<T>[] cds = ColumnDescs.newColumnDescs(header.length);

        int i = 0;
        HEADER: for (final String headerElem : header) {
            for (final Iterator<ColumnDesc<T>> it = tmpCds.iterator(); it
                    .hasNext();) {
                final ColumnDesc<T> cd = it.next();
                final ColumnName name = cd.getName();
                if (name.labelEquals(headerElem)) {
                    cds[i] = cd;
                    i++;
                    it.remove();
                    continue HEADER;
                }
            }
            /*
             * ヘッダ行に存在しない列は無視する
             */
            //throw new RuntimeException("headerElem=" + headerElem);
            logger.debug("ignore column=[{}]", headerElem);
            cds[i] = new DefaultRecordDesc.IgnoreColumnDesc<T>();
            i++;
        }

        final DefaultRecordDesc<T> copy = createCopy();

        copy.columnDescs_ = cds;

        if (!tmpCds.isEmpty()) {
            logger.debug("remain ColumnDescs: {}", tmpCds.size());
            final ColumnDesc<T>[] newColumnDescs = ColumnDescs
                    .newColumnDescs(tmpCds.size());
            tmpCds.toArray(newColumnDescs);
            copy.ignoredColumnDescs_ = newColumnDescs;
        }

        return copy;
    }

    /*
     * CSVを書くとき。
     * Map Writerのときに使われる。
     */
    @Override
    public RecordDesc<T> setupByBean(final T bean) {
        return this;
    }

    @Override
    public T newInstance() {
        return recordType_.newInstance();
    }

    private DefaultRecordDesc<T> createCopy() {
        final DefaultRecordDesc<T> copy = new DefaultRecordDesc<T>(
                columnDescs_, orderSpecified_, recordType_);
        return copy;
    }

    private static class IgnoreColumnDesc<T> implements ColumnDesc<T> {

        @Override
        public ColumnName getName() {
            return null;
        }

        @Override
        public String getValue(final T bean) {
            return null;
        }

        @Override
        public void setValue(final T bean, final String value) {
        }

    }

}
