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

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.internal.CollectionsUtil;

public class DefaultConverterRepository implements ConverterRepository {

    /*
     * キーは、Java側プロパティの型
     */
    private final Map<ConverterKey, Converter> propertyTypeMap_ = CollectionsUtil.newHashMap();
    private final Class<?> stringArrayClass_ = new String[0].getClass();

    /*
     * BEAN情報 + Property情報
     * - BEANの型 + Propertyの型
     *
     * Property情報
     * - Propertyの型
     */

    @Override
    public Converter detect(final CsvColumnDef columnDef) {
        final Class<?> propertyType = columnDef.getPropertyType();
        final Converter converter = _detectByType(propertyType);
        return converter;
    }

    @Override
    public Converter detect(final CsvColumnsDef columnsDef) {
        //        final List<CsvColumnDef> columnDefs = columnsDef.getColumnDefs();
        //        final Object[] outerTypes = new Object[columnDefs.size()];
        //        for (final CsvColumnDef columnDef : columnDefs) {
        //        }
        final Class<?> propertyType = columnsDef.getPropertyType();
        final Converter converter = propertyTypeMap_.get(new ConverterKey(propertyType, stringArrayClass_));
        return converter;
    }

    private Converter _detectByType(final Class<?> propertyType) {
        final Converter converter = propertyTypeMap_.get(new ConverterKey(propertyType, String.class));
        return converter;
    }

    public void register(final Converter converter) {
        final Type[] genericInterfaces = converter.getClass().getGenericInterfaces();
        for (final Type type : genericInterfaces) {
            final ParameterizedType pType = (ParameterizedType) type;
            final Type rawType = pType.getRawType();
            if (rawType instanceof Class) {
                if (Converter.class.isAssignableFrom((Class) rawType)) {
                    final Type[] actualTypeArguments = pType.getActualTypeArguments();
                    final Class<?> propertyType = toPropertyType(actualTypeArguments[0]);
                    final Class<?> outerType = toOuterTypes(actualTypeArguments[1]);
                    propertyTypeMap_.put(new ConverterKey(propertyType, outerType), converter);
                    return;
                }
            }
        }
    }

    // XXX 型チェックが必要かも
    private Class<?> toPropertyType(final Type inType) {
        return (Class<?>) inType;
    }

    private Class<?> toOuterTypes(final Type outType) {
        if (outType instanceof Class) {
            return (Class<?>) outType;
        }
        if (outType instanceof GenericArrayType) {
            final GenericArrayType gaType = (GenericArrayType) outType;
            final Type genericComponentType = gaType.getGenericComponentType();
            // outer側の要素数を取得したかったが、String[] までしか取得できないことに気づいた。
            final Class clazz = (Class) genericComponentType;
            if (String.class.equals(clazz)) {
                return stringArrayClass_;
            }
            //            final Object newInstance = Array.newInstance(clazz, 0);
            //            return newInstance.getClass();
            throw new UnsupportedOperationException("genericComponentType: " + clazz);
        }

        throw new UnsupportedOperationException("type: " + outType);
    }

    private static class ConverterKey {

        private final Class<?> propertyType_;
        private final Class<?> outerType_;
        private final int hashCode_;

        ConverterKey(final Class<?> propertyType, final Class<?> outerType) {
            propertyType_ = propertyType;
            outerType_ = outerType;

            final int coefficient = 37;
            int c = 17;
            c = c * coefficient + propertyType.hashCode();
            c = c * coefficient + outerType.hashCode();
            hashCode_ = c;
        }

        @Override
        public int hashCode() {
            return hashCode_;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ConverterKey)) {
                return false;
            }
            final ConverterKey another = (ConverterKey) obj;
            if (!propertyType_.equals(another.propertyType_)) {
                return false;
            }
            if (!outerType_.equals(another.outerType_)) {
                return false;
            }
            return true;
        }

    }

}
