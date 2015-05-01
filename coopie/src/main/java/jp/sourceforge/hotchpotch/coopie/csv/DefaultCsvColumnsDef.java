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

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.internal.CollectionsUtil;
import jp.sourceforge.hotchpotch.coopie.internal.PropertyDesc;
import jp.sourceforge.hotchpotch.coopie.util.Text;

class DefaultCsvColumnsDef implements CsvColumnsDef {

    private String propertyName_;
    private Class<?> propertyType_;
    private Converter<?, ?> converter_ = PassthroughStringConverter.getInstance();
    private final List<CsvColumnDef> columnDefs_ = CollectionsUtil.newArrayList();

    public void setup(final CsvColumns columns, final PropertyDesc pd) {
        for (final CsvColumn column : columns.value()) {
            final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
            if (Text.isBlank(column.label())) {
                columnDef.setLabel(pd.getPropertyName());
            } else {
                columnDef.setLabel(column.label());
            }
            columnDef.setOrder(column.order());
            addColumnDef(columnDef);
        }
        setPropertyName(pd.getPropertyName());
        setPropertyType(pd.getPropertyType());
    }

    @Override
    public List<CsvColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    public void addColumnDef(final CsvColumnDef columnDef) {
        columnDefs_.add(columnDef);
    }

    @Override
    public boolean hasConverter() {
        if (converter_ == null) {
            return false;
        }
        if (converter_ instanceof PassthroughStringConverter) {
            return false;
        }
        return true;
    }

    @Override
    public Converter<?, ?> getConverter() {
        return converter_;
    }

    @Override
    public void setConverter(final Converter<?, ?> converter) {
        converter_ = converter;
    }

    @Override
    public String getPropertyName() {
        return propertyName_;
    }

    @Override
    public void setPropertyName(final String propertyName) {
        propertyName_ = propertyName;
    }

    @Override
    public Class<?> getPropertyType() {
        return propertyType_;
    }

    public void setPropertyType(final Class<?> propertyType) {
        propertyType_ = propertyType;
    }

}
