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

import java.util.Objects;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout.PropertyNotFoundException;
import jp.sourceforge.hotchpotch.coopie.internal.BeanDesc;
import jp.sourceforge.hotchpotch.coopie.internal.PropertyDesc;

public class BeanPropertyBinding<BEAN, PROP> implements PropertyBinding<BEAN, PROP> {

    private final PropertyDesc<BEAN, PROP> propertyDesc_;

    public BeanPropertyBinding(final PropertyDesc<BEAN, PROP> propertyDesc) {
        propertyDesc_ = propertyDesc;
    }

    @Override
    public void setValue(final BEAN bean, final PROP value) {
        propertyDesc_.setValue(bean, value);
    }

    @Override
    public PROP getValue(final BEAN bean) {
        final PROP value = propertyDesc_.getValue(bean);
        return value;
    }

    public static class Factory<BEAN> implements PropertyBindingFactory<BEAN> {

        private final BeanDesc<BEAN> beanDesc_;

        public Factory(final BeanDesc<BEAN> beanDesc) {
            beanDesc_ = beanDesc;
        }

        @Override
        public <PROP> PropertyBinding<BEAN, PROP> getPropertyBinding(final String name) {
            Objects.requireNonNull(name, "name");

            final PropertyDesc<BEAN, PROP> pd = beanDesc_.getPropertyDesc(name);
            if (pd != null) {
                return new BeanPropertyBinding<>(pd);
            }

            final Class<? extends BEAN> clazz = beanDesc_.getBeanClass();
            final String className = clazz.getName();
            throw new PropertyNotFoundException("property not found:<" + name + "> for class:<" + className + ">");
        }

    }

}
