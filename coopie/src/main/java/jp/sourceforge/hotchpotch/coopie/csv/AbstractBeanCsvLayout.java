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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.util.Annotations;
import jp.sourceforge.hotchpotch.coopie.util.PropertyAnnotationReader;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public abstract class AbstractBeanCsvLayout<BEAN> extends AbstractCsvLayout<BEAN> {

    private final BeanDesc<BEAN> beanDesc_;
    private CsvRecordDefCustomizer customizer_ = EmptyRecordDefCustomizer.getInstance();
    private PropertyAnnotationReader propertyAnnotationReader_ = Annotations.getPropertyAnnotationReader();

    public AbstractBeanCsvLayout(final Class<BEAN> beanClass) {
        beanDesc_ = BeanDescFactory.getBeanDesc(beanClass);
    }

    protected void prepareBuild() {
        if (getRecordDesc() == null) {
            final CsvRecordDef recordDef = recordDef();
            registerConverter(recordDef);
            customizer_.customize(recordDef);
            final RecordDesc<BEAN> recordDesc = createRecordDesc(recordDef);
            setRecordDesc(recordDesc);
        }

        if (getRecordDesc() == null) {
            throw new AssertionError("recordDesc");
        }
    }

    private void registerConverter(final CsvRecordDef recordDef) {
        final ConverterRepository converterRepository = getConverterRepository();
        for (final CsvColumnDef csvColumnDef : recordDef.getColumnDefs()) {
            if (csvColumnDef.hasConverter()) {
                continue;
            }
            final Converter converter = converterRepository.detect(csvColumnDef);
            if (converter != null) {
                csvColumnDef.setConverter(converter);
            }
        }
        for (final CsvColumnsDef csvColumnsDef : recordDef.getColumnsDefs()) {
            if (csvColumnsDef.hasConverter()) {
                continue;
            }
            final Converter converter = converterRepository.detect(csvColumnsDef);
            if (converter != null) {
                csvColumnsDef.setConverter(converter);
            }
        }
    }

    @Override
    protected PropertyBindingFactory<BEAN> createPropertyBindingFactory() {
        return new BeanPropertyBinding.Factory<BEAN>(beanDesc_);
    }

    @Override
    protected BeanRecordType<BEAN> createRecordType() {
        return new BeanRecordType<BEAN>(beanDesc_);
    }

    private CsvRecordDef recordDef() {
        if (getRecordDef() == null) {
            final CsvRecordDef r = createRecordDef();
            setRecordDef(r);
        }
        return getRecordDef();
    }

    private CsvRecordDef createRecordDef() {
        /*
         * アノテーションが付いている場合は、アノテーションを優先する
         */
        CsvRecordDef recordDef = createRecordDefByAnnotation();
        if (recordDef == null) {
            /*
             * beanの全プロパティを対象に。
             */
            recordDef = createRecordDefByProperties();
        }
        if (recordDef == null) {
            throw new AssertionError("recordDef");
        }
        return recordDef;
    }

    private CsvRecordDef createRecordDefByAnnotation() {
        final DefaultCsvRecordDef recordDef = new DefaultCsvRecordDef();
        final List<PropertyDesc<BEAN>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<BEAN> pd : pds) {
            final CsvColumns columns = getPropertyAnnotationReader().getAnnotation(pd, CsvColumns.class);
            if (columns != null) {
                final DefaultCsvColumnsDef columnsDef = new DefaultCsvColumnsDef();
                columnsDef.setup(columns, pd);
                recordDef.addColumnsDef(columnsDef);
                continue;
            }
            // TODO: CsvColumnとCsvColumnsの両方があったら例外にすること

            final CsvColumn column = getPropertyAnnotationReader().getAnnotation(pd, CsvColumn.class);
            if (column != null) {
                final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
                columnDef.setup(column, pd);
                recordDef.addColumnDef(columnDef);
            }
        }

        if (recordDef.isEmpty()) {
            return null;
        }

        Collections.sort(recordDef.getColumnDefs(), CsvColumnDefComparator.getInstance());
        return recordDef;
    }

    private CsvRecordDef createRecordDefByProperties() {
        final DefaultCsvRecordDef recordDef = new DefaultCsvRecordDef();
        final List<PropertyDesc<BEAN>> pds = beanDesc_.getAllPropertyDesc();
        for (final PropertyDesc<BEAN> pd : pds) {
            final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
            // orderは未指定とする
            columnDef.setup(pd);
            recordDef.addColumnDef(columnDef);
        }
        return recordDef;
    }

    public void setCustomizer(final CsvRecordDefCustomizer columnCustomizer) {
        customizer_ = columnCustomizer;
    }

    public PropertyAnnotationReader getPropertyAnnotationReader() {
        return propertyAnnotationReader_;
    }

    public void setPropertyAnnotationReader(final PropertyAnnotationReader propertyAnnotationReader) {
        propertyAnnotationReader_ = propertyAnnotationReader;
    }

    static class CsvColumnDefComparator implements Comparator<CsvColumnDef> {

        private static CsvColumnDefComparator INSTANCE = new CsvColumnDefComparator();

        public static CsvColumnDefComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int compare(final CsvColumnDef o1, final CsvColumnDef o2) {
            // orderが小さい方を左側に
            final int ret = o1.getOrder() - o2.getOrder();
            return ret;
        }

    }

    static class PropertyNotFoundException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public PropertyNotFoundException(final String message) {
            super(message);
        }

    }

}
