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

import jp.sourceforge.hotchpotch.coopie.internal.PropertyDesc;
import jp.sourceforge.hotchpotch.coopie.util.Text;

class DefaultCsvColumnDef implements CsvColumnDef, Comparable<CsvColumnDef> {

    private String label_;
    private String propertyName_;
    private Class<?> propertyType_;

    private int order_;
    private Converter<?, ?> converter_ = PassthroughStringConverter.getInstance();
    private ColumnNameMatcher columnNameMatcher_ = ExactNameMatcher.getInstance();

    public void setup(final CsvColumn column, final PropertyDesc pd) {
        if (Text.isBlank(column.label())) {
            setLabel(pd.getPropertyName());
        } else {
            setLabel(column.label());
        }
        setOrder(column.order());
        setPropertyName(pd.getPropertyName());
        setPropertyType(pd.getPropertyType());
    }

    public void setup(final PropertyDesc pd) {
        final String propertyName = pd.getPropertyName();
        setLabel(propertyName);
        setPropertyName(pd.getPropertyName());
        setPropertyType(pd.getPropertyType());
    }

    @Override
    public String getLabel() {
        return label_;
    }

    @Override
    public void setLabel(final String label) {
        label_ = label;
    }

    @Override
    public int getOrder() {
        return order_;
    }

    @Override
    public void setOrder(final int order) {
        order_ = order;
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

    @Override
    public int compareTo(final CsvColumnDef o) {
        // orderが小さい方を左側に
        final int ret = getOrder() - o.getOrder();
        return ret;
    }

    @Override
    public ColumnNameMatcher getColumnNameMatcher() {
        return columnNameMatcher_;
    }

    @Override
    public void setColumnNameMatcher(final ColumnNameMatcher columnNameMatcher) {
        columnNameMatcher_ = columnNameMatcher;
    }

}
