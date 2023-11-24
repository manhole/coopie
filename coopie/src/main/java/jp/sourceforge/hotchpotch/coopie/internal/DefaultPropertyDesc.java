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

package jp.sourceforge.hotchpotch.coopie.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import jp.sourceforge.hotchpotch.coopie.CoopieException;

/**
 * @author manhole
 */
class DefaultPropertyDesc<BEAN, PROPERTY> implements PropertyDesc<BEAN, PROPERTY> {

    private Supplier<BeanDesc<BEAN>> beanDescSupplier_;
    private String propertyName_;
    private Class<PROPERTY> propertyType_;
    private Method readMethod_;
    private Method writeMethod_;

    @Override
    public String getPropertyName() {
        return propertyName_;
    }

    protected void setPropertyName(final String propertyName) {
        propertyName_ = propertyName;
    }

    @Override
    public Class<PROPERTY> getPropertyType() {
        return propertyType_;
    }

    protected void setPropertyType(final Class<PROPERTY> propertyType) {
        propertyType_ = propertyType;
    }

    @Override
    public boolean isReadable() {
        return readMethod_ != null;
    }

    @Override
    public boolean isWritable() {
        return writeMethod_ != null;
    }

    @Override
    public void setValue(final BEAN instance, final PROPERTY value) {
        invokeMethod(writeMethod_, instance, value);
    }

    @Override
    public PROPERTY getValue(final BEAN instance) {
        return invokeMethod(readMethod_, instance);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokeMethod(final Method method, final Object obj, final Object... args) {
        try {
            return (T) method.invoke(obj, args);
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new CoopieException(e);
        }
    }

    @Override
    public BeanDesc<BEAN> getBeanDesc() {
        return beanDescSupplier_.get();
    }

    protected void setBeanDescSupplier(final Supplier<BeanDesc<BEAN>> beanDesc) {
        beanDescSupplier_ = beanDesc;
    }

    @Override
    public Method getReadMethod() {
        return readMethod_;
    }

    protected void setReadMethod(final Method readMethod) {
        readMethod_ = readMethod;
    }

    @Override
    public Method getWriteMethod() {
        return writeMethod_;
    }

    protected void setWriteMethod(final Method writeMethod) {
        writeMethod_ = writeMethod;
    }

    public static <BEAN, PROPERTY> Builder<BEAN, PROPERTY> builder() {
        return new Builder<>();
    }

    public static class Builder<BEAN, PROPERTY> {

        private Supplier<BeanDesc<BEAN>> beanDescSupplier_;
        private String propertyName_;
        private Method readMethod_;
        private Method writeMethod_;
        private Class<PROPERTY> propertyType_;
        private boolean valid_ = false;

        DefaultPropertyDesc<BEAN, ?> build() {
            if (!isValid()) {
                throw new IllegalStateException("invalid");
            }
            final DefaultPropertyDesc<BEAN, PROPERTY> pd = new DefaultPropertyDesc<>();
            pd.setBeanDescSupplier(beanDescSupplier_);
            pd.setPropertyName(propertyName_);
            pd.setPropertyType(propertyType_);
            pd.setReadMethod(readMethod_);
            pd.setWriteMethod(writeMethod_);
            return pd;
        }

        public Builder<BEAN, PROPERTY> beanDescSupplier(final Supplier<BeanDesc<BEAN>> beanDescSupplier) {
            beanDescSupplier_ = beanDescSupplier;
            return this;
        }

        public Builder<BEAN, PROPERTY> propertyName(final String propertyName) {
            propertyName_ = propertyName;
            return this;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Builder<BEAN, PROPERTY> readMethod(final Method readMethod) {
            readMethod_ = readMethod;
            final Class type = readMethod.getReturnType();
            if (writeMethod_ == null) {
                propertyType_ = type;
                valid_ = true;
            } else {
                valid_ = isSameType(type, propertyType_);
            }
            return this;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        public Builder<BEAN, PROPERTY> writeMethod(final Method writeMethod) {
            writeMethod_ = writeMethod;
            final Class type = writeMethod.getParameterTypes()[0];
            if (readMethod_ == null) {
                propertyType_ = type;
                valid_ = true;
            } else {
                valid_ = isSameType(type, propertyType_);
            }
            return this;
        }

        private boolean isSameType(final Class<?> a, final Class<?> b) {
            if (a == null || b == null) {
                return false;
            }
            return a.equals(b);
        }

        public boolean isValid() {
            return valid_;
        }

    }

}
