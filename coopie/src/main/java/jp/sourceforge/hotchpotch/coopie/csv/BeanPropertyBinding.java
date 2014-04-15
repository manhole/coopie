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

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout.PropertyNotFoundException;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.ClassDesc;
import org.t2framework.commons.meta.MethodDesc;
import org.t2framework.commons.meta.PropertyDesc;

public class BeanPropertyBinding<BEAN, PROP> implements PropertyBinding<BEAN, PROP> {

    private final MethodDesc writeMethodDesc_;
    private final MethodDesc readMethodDesc_;

    public BeanPropertyBinding(final PropertyDesc<BEAN> propertyDesc) {
        writeMethodDesc_ = propertyDesc.getWriteMethodDesc();
        readMethodDesc_ = propertyDesc.getReadMethodDesc();
    }

    @Override
    public void setValue(final BEAN bean, final PROP value) {
        /*
         * PropertyDesc#setValueだと勝手に変換されるので、
         * 変換されないようMethodDescを取り出して使用する。
         */
        writeMethodDesc_.invoke(bean, new Object[] { value });
    }

    @Override
    public PROP getValue(final BEAN bean) {
        final Object value = readMethodDesc_.invoke(bean, null);
        @SuppressWarnings("unchecked")
        final PROP v = (PROP) value;
        return v;
    }

    public static class Factory<BEAN> implements PropertyBindingFactory<BEAN> {

        private final BeanDesc<BEAN> beanDesc_;

        public Factory(final BeanDesc<BEAN> beanDesc) {
            beanDesc_ = beanDesc;
        }

        @Override
        public <PROP> PropertyBinding<BEAN, PROP> getPropertyBinding(final String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            final PropertyDesc<BEAN> pd = beanDesc_.getPropertyDesc(name);
            if (pd == null) {
                final ClassDesc<BEAN> classDesc = beanDesc_.getClassDesc();
                final Class<? extends BEAN> concreteClass = classDesc.getConcreteClass();
                final String className = concreteClass.getName();
                throw new PropertyNotFoundException("property not found:<" + name + "> for class:<" + className + ">");
            }
            return new BeanPropertyBinding<>(pd);
        }

    }

}
