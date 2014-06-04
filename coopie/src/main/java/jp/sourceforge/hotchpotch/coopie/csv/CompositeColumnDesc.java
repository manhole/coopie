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

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.MethodDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

public class CompositeColumnDesc<BEAN> {

    private List<ColumnName> columnNames_;
    private PropertyBinding propertyBinding_;

    @SuppressWarnings("rawtypes")
    private Converter converter_;
    private Map<ColumnName, String> getValues_;
    private Map<ColumnName, String> setValues_;

    public ColumnDesc<BEAN>[] getColumnDescs() {
        final ColumnDesc<BEAN>[] cds = ColumnDescs.newColumnDescs(columnNames_.size());
        int i = 0;
        for (final ColumnName columnName : columnNames_) {
            cds[i] = new Adapter(columnName);
            i++;
        }
        return cds;
    }

    public Converter getConverter() {
        return converter_;
    }

    public void setColumnNames(final List<ColumnName> columnNames) {
        columnNames_ = columnNames;
    }

    public void setPropertyBinding(final PropertyBinding propertyBinding) {
        propertyBinding_ = propertyBinding;
    }

    public void setConverter(final Converter converter) {
        converter_ = converter;
    }

    private String getValue(final ColumnName columnName, final BEAN bean) {
        if (getValues_ == null || !getValues_.containsKey(columnName)) {
            final Object from = propertyBinding_.getValue(bean);

            // TODO 戻り値が配列ではない場合
            final Object[] to = (Object[]) converter_.convertTo(from);
            getValues_ = CollectionsUtil.newHashMap();
            {
                int i = 0;
                for (final ColumnName name : columnNames_) {
                    final Object value = to[i];
                    final String s = StringUtil.toString(value);
                    getValues_.put(name, s);
                    i++;
                }
            }
        }

        final String v = getValues_.remove(columnName);
        if (getValues_.isEmpty()) {
            getValues_ = null;
        }
        return v;
    }

    private void setValue(final ColumnName columnName, final BEAN bean, final String value) {
        if (setValues_ == null || setValues_.containsKey(columnName)) {
            setValues_ = CollectionsUtil.newHashMap();
        }
        setValues_.put(columnName, value);

        if (setValues_.size() == columnNames_.size()) {
            // TODO 引数が配列ではない場合
            Class<?> componentType;
            {
                final MethodDesc methodDesc = BeanDescFactory.getBeanDesc(converter_.getClass()).getMethodDesc(
                        "convertFrom");
                final Class<?>[] parameterTypes = methodDesc.getParameterTypes();
                componentType = parameterTypes[0].getComponentType();
            }
            final Object[] from = (Object[]) Array.newInstance(componentType, columnNames_.size());
            {
                int i = 0;
                for (final ColumnName name : columnNames_) {
                    final String s = setValues_.get(name);
                    from[i] = s;
                    i++;
                }
            }

            final Object to = converter_.convertFrom(from);
            setValues_ = null;
            propertyBinding_.setValue(bean, to);
        }
    }

    public static <BEAN> ColumnDesc<BEAN>[] newCompositeColumnDesc(final List<ColumnName> names,
            final PropertyBinding<BEAN, Object> propertyBinding, final Converter converter) {

        final CompositeColumnDesc ccd = new CompositeColumnDesc();
        ccd.setPropertyBinding(propertyBinding);
        ccd.setColumnNames(names);
        ccd.setConverter(converter);
        return ccd.getColumnDescs();
    }

    class Adapter implements ColumnDesc<BEAN> {

        private final ColumnName columnName_;

        Adapter(final ColumnName columnName) {
            columnName_ = columnName;
        }

        @Override
        public ColumnName getName() {
            return columnName_;
        }

        @Override
        public String getValue(final BEAN bean) {
            final String v = CompositeColumnDesc.this.getValue(columnName_, bean);
            return v;
        }

        @Override
        public void setValue(final BEAN bean, final String value) {
            CompositeColumnDesc.this.setValue(columnName_, bean, value);
        }

    }

}
