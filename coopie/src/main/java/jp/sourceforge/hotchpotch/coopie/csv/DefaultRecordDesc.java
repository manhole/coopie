/*
 * Copyright 2010 manhole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.internal.CollectionsUtil;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public class DefaultRecordDesc<BEAN> implements RecordDesc<BEAN> {

    private static final Logger logger = LoggerFactory.getLogger();

    private ColumnDesc<BEAN>[] columnDescs_;
    private final OrderSpecified orderSpecified_;
    private final RecordType<BEAN> recordType_;
    private ColumnDesc<BEAN>[] ignoredColumnDescs_ = ColumnDescs.newColumnDescs(0);

    public DefaultRecordDesc(final ColumnDesc<BEAN>[] columnDescs, final OrderSpecified orderSpecified,
            final RecordType<BEAN> recordType) {
        columnDescs_ = columnDescs;
        orderSpecified_ = orderSpecified;
        recordType_ = recordType;
    }

    protected ColumnDesc<BEAN>[] getColumnDescs() {
        return columnDescs_;
    }

    @Override
    public String[] getHeaderValues() {
        final ColumnDesc<BEAN>[] cds = getColumnDescs();
        if (cds == null) {
            return null;
        }
        final String[] line = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<BEAN> cd = cds[i];
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
    public String[] getValues(final BEAN bean) {
        final ColumnDesc<BEAN>[] cds = getColumnDescs();
        final String[] values = new String[cds.length];
        for (int i = 0; i < cds.length; i++) {
            final ColumnDesc<BEAN> cd = cds[i];
            final String value = cd.getValue(bean);
            values[i] = value(value);
        }
        return values;
    }

    @Override
    public void setValues(final BEAN bean, final String[] values) {
        final ColumnDesc<BEAN>[] cds = getColumnDescs();
        int i = 0;
        for (; i < values.length; i++) {
            final String value = value(values[i]);
            final ColumnDesc<BEAN> cd = cds[i];
            cd.setValue(bean, value);
        }
        for (; i < cds.length; i++) {
            final String value = null;
            final ColumnDesc<BEAN> cd = cds[i];
            cd.setValue(bean, value);
        }
        for (final ColumnDesc<BEAN> cd : ignoredColumnDescs_) {
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
    public RecordDesc<BEAN> setupByHeader(final String[] headerValues) {
        logger.debug("setupByHeader: {}", Arrays.toString(headerValues));
        /*
         * ColumnDescをヘッダの順序に合わせてソートし直す。
         */
        final List<ColumnDesc<BEAN>> tmpCds = CollectionsUtil.newArrayList();
        Collections.addAll(tmpCds, getColumnDescs());
        final ColumnDesc<BEAN>[] cds = ColumnDescs.newColumnDescs(headerValues.length);

        int i = 0;
        HEADER: for (final String header : headerValues) {
            for (final Iterator<ColumnDesc<BEAN>> it = tmpCds.iterator(); it.hasNext();) {
                final ColumnDesc<BEAN> cd = it.next();
                final ColumnName name = cd.getName();
                if (name.labelEquals(header)) {
                    cds[i] = cd;
                    i++;
                    it.remove();
                    continue HEADER;
                }
            }
            /*
             * ヘッダ行に存在しない列は無視する
             */
            //throw new RuntimeException("header=" + header);
            logger.debug("ignore column=[{}]", header);
            cds[i] = DefaultRecordDesc.IgnoreColumnDesc.getInstance();
            i++;
        }

        final DefaultRecordDesc<BEAN> copy = createCopy();
        copy.columnDescs_ = cds;

        if (!tmpCds.isEmpty()) {
            logger.debug("remain ColumnDescs: {}", tmpCds.size());
            final ColumnDesc<BEAN>[] newColumnDescs = ColumnDescs.newColumnDescs(tmpCds.size());
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
    public RecordDesc<BEAN> setupByBean(final BEAN bean) {
        return this;
    }

    @Override
    public BEAN newInstance() {
        return recordType_.newInstance();
    }

    private DefaultRecordDesc<BEAN> createCopy() {
        final DefaultRecordDesc<BEAN> copy = new DefaultRecordDesc<>(columnDescs_, orderSpecified_, recordType_);
        return copy;
    }

    private static class IgnoreColumnDesc<BEAN> implements ColumnDesc<BEAN> {

        private static final IgnoreColumnDesc INSTANCE = new IgnoreColumnDesc();

        public static <BEAN> IgnoreColumnDesc<BEAN> getInstance() {
            return INSTANCE;
        }

        @Override
        public ColumnName getName() {
            return null;
        }

        @Override
        public String getValue(final BEAN bean) {
            return null;
        }

        @Override
        public void setValue(final BEAN bean, final String value) {
        }

    }

}
