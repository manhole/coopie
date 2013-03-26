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

import org.t2framework.commons.util.StringUtil;

public class DefaultColumnDesc<BEAN> implements ColumnDesc<BEAN> {

    /**
     * CSV列名。
     */
    private ColumnName columnName_;
    private PropertyBinding propertyBinding_;
    private Converter converter_;

    @Override
    public ColumnName getName() {
        return columnName_;
    }

    public void setName(final ColumnName name) {
        columnName_ = name;
    }

    public PropertyBinding getPropertyBinding() {
        return propertyBinding_;
    }

    public void setPropertyBinding(final PropertyBinding propertyBinding) {
        propertyBinding_ = propertyBinding;
    }

    public Converter getConverter() {
        return converter_;
    }

    public void setConverter(final Converter converter) {
        converter_ = converter;
    }

    @Override
    public String getValue(final BEAN bean) {
        final Object from = propertyBinding_.getValue(bean);
        final Object to = converter_.convertTo(from);
        return StringUtil.toString(to);
    }

    @Override
    public void setValue(final BEAN bean, final String value) {
        final String from = value;
        final Object to = converter_.convertFrom(from);
        propertyBinding_.setValue(bean, to);
    }

    public static <T> ColumnDesc<T> newColumnDesc(final ColumnName columnName,
            final PropertyBinding propertyBinding, final Converter converter) {
        final DefaultColumnDesc<T> cd = new DefaultColumnDesc<T>();
        cd.setName(columnName);
        cd.setPropertyBinding(propertyBinding);
        cd.setConverter(converter);
        return cd;
    }

}
