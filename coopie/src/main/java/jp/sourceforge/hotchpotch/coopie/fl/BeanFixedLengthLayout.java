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

package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.BeanPropertyBinding;
import jp.sourceforge.hotchpotch.coopie.csv.BeanRecordType;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.DefaultRecordWriter;
import jp.sourceforge.hotchpotch.coopie.csv.ElementEditor;
import jp.sourceforge.hotchpotch.coopie.csv.ElementInOut;
import jp.sourceforge.hotchpotch.coopie.csv.PropertyBindingFactory;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc;
import jp.sourceforge.hotchpotch.coopie.csv.RecordInOut;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordType;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;
import jp.sourceforge.hotchpotch.coopie.util.Annotations;
import jp.sourceforge.hotchpotch.coopie.util.PropertyAnnotationReader;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public class BeanFixedLengthLayout<BEAN> extends AbstractFixedLengthLayout<BEAN> {

    private final BeanDesc<BEAN> beanDesc_;
    private PropertyAnnotationReader propertyAnnotationReader_ = Annotations.getPropertyAnnotationReader();

    public static <BEAN> BeanFixedLengthLayout<BEAN> getInstance(final Class<BEAN> beanClass) {
        final BeanFixedLengthLayout<BEAN> instance = new BeanFixedLengthLayout<>(beanClass);
        return instance;
    }

    public BeanFixedLengthLayout(final Class<BEAN> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    public RecordInOut<BEAN> build() {
        prepareOpen();

        final BeanFixedLengthRecordInOut<BEAN> obj = new BeanFixedLengthRecordInOut<>();
        obj.recordDesc_ = getRecordDesc();
        obj.withHeader_ = isWithHeader();
        obj.elementInOut_ = createElementInOut();
        // ある方が良いかな?
        //obj.elementReaderHandler_ = getElementReaderHandler();
        obj.elementEditor_ = getElementEditor();
        return obj;
    }

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            /*
             * アノテーションが付いている場合は、アノテーションから構築する
             */
            final FixedLengthRecordDef recordDef = recordDef();
            // TODO customizer_.customize(recordDef);
            {
                final FixedLengthElementDesc[] elementDescs = recordDefToElementDescs(recordDef);
                setFixedLengthElementDescs(elementDescs);
            }
            {
                final RecordDesc<BEAN> recordDesc = createRecordDesc(recordDef);
                setRecordDesc(recordDesc);
            }
        }

        if (getRecordDesc() == null) {
            throw new AssertionError("recordDesc");
        }
    }

    @Override
    protected PropertyBindingFactory<BEAN> createPropertyBindingFactory() {
        return new BeanPropertyBinding.Factory<>(beanDesc_);
    }

    @Override
    protected RecordType<BEAN> createRecordType() {
        return new BeanRecordType<>(beanDesc_);
    }

    private FixedLengthRecordDef recordDef() {
        if (getRecordDef() == null) {
            final FixedLengthRecordDef r = createRecordDef();
            setRecordDef(r);
        }
        return getRecordDef();
    }

    private FixedLengthRecordDef createRecordDef() {
        /*
         * アノテーションから作成する
         */
        final FixedLengthRecordDef recordDef = createRecordDefByAnnotation();
        if (recordDef == null) {
            throw new AssertionError("recordDef");
        }
        return recordDef;
    }

    private FixedLengthRecordDef createRecordDefByAnnotation() {
        final DefaultFixedLengthRecordDef recordDef = new DefaultFixedLengthRecordDef();
        final List<PropertyDesc<BEAN>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<BEAN> pd : pds) {
            final FixedLengthColumn column = getPropertyAnnotationReader().getAnnotation(pd, FixedLengthColumn.class);
            if (column == null) {
                continue;
            }
            final DefaultFixedLengthColumnDef columnDef = new DefaultFixedLengthColumnDef();
            columnDef.setPropertyName(pd.getPropertyName());
            columnDef.setBeginIndex(column.beginIndex());
            columnDef.setEndIndex(column.endIndex());
            recordDef.addColumnDef(columnDef);
        }
        if (recordDef.isEmpty()) {
            return null;
        }

        return recordDef;
    }

    public PropertyAnnotationReader getPropertyAnnotationReader() {
        return propertyAnnotationReader_;
    }

    public void setPropertyAnnotationReader(final PropertyAnnotationReader propertyAnnotationReader) {
        propertyAnnotationReader_ = propertyAnnotationReader;
    }

    protected static class BeanFixedLengthRecordInOut<BEAN> implements RecordInOut<BEAN> {

        private RecordDesc<BEAN> recordDesc_;
        private boolean withHeader_;
        private ElementInOut elementInOut_;
        private ElementEditor elementEditor_;

        @Override
        public RecordReader<BEAN> openReader(final Readable readable) {
            if (readable == null) {
                throw new NullPointerException("readable");
            }

            final DefaultRecordReader<BEAN> r = new DefaultRecordReader<>(recordDesc_);
            r.setWithHeader(withHeader_);
            r.setElementInOut(elementInOut_);
            r.setElementEditor(elementEditor_);
            // TODO openで例外時にcloseすること
            r.open(readable);
            return r;
        }

        @Override
        public RecordWriter<BEAN> openWriter(final Appendable appendable) {
            if (appendable == null) {
                throw new NullPointerException("appendable");
            }

            final DefaultRecordWriter<BEAN> w = new DefaultRecordWriter<>(recordDesc_);
            w.setWithHeader(withHeader_);
            w.setElementInOut(elementInOut_);
            // TODO openで例外時にcloseすること
            w.open(appendable);
            return w;
        }
    }

}
