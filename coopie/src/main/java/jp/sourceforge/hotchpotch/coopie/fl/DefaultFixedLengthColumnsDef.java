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

import jp.sourceforge.hotchpotch.coopie.csv.Converter;
import jp.sourceforge.hotchpotch.coopie.csv.PassthroughStringConverter;

import org.t2framework.commons.util.CollectionsUtil;

class DefaultFixedLengthColumnsDef implements FixedLengthColumnsDef {

    private String propertyName_;
    private Converter<?, ?> converter_ = PassthroughStringConverter.getInstance();
    private final List<FixedLengthColumnDef> columnDefs_ = CollectionsUtil.newArrayList();

    @Override
    public List<FixedLengthColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    public void addColumnDef(final FixedLengthColumnDef columnDef) {
        columnDefs_.add(columnDef);
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
    public Converter<?, ?> getConverter() {
        return converter_;
    }

    @Override
    public void setConverter(final Converter<?, ?> converter) {
        converter_ = converter;
    }

}
