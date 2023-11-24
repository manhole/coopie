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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.sourceforge.hotchpotch.coopie.CoopieException;
import jp.sourceforge.hotchpotch.coopie.util.Text;

/**
 * @author manhole
 */
class DefaultBeanDesc<BEAN> implements BeanDesc<BEAN> {

    private Class<BEAN> beanClass_;
    private final Map<String, PropertyDesc<BEAN, ?>> properties_ = CollectionsUtil.newHashMap();
    private BeanMethods methods_;

    protected DefaultBeanDesc() {
    }

    @Override
    public Class<BEAN> getBeanClass() {
        return beanClass_;
    }

    protected void setBeanClass(final Class<BEAN> beanClass) {
        beanClass_ = beanClass;
    }

    @Override
    public boolean hasPropertyDesc(final String propertyName) {
        final String key = propertyName(propertyName);
        return properties_.containsKey(key);
    }

    private String propertyName(final String propertyName) {
        if (propertyName == null) {
            return null;
        }
        return propertyName.toLowerCase();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <PROPERTY> PropertyDesc<BEAN, PROPERTY> getPropertyDesc(final String propertyName) {
        final String key = propertyName(propertyName);
        return (PropertyDesc<BEAN, PROPERTY>) properties_.get(key);
    }

    @Override
    public BEAN newInstance() {
        try {
            return beanClass_.newInstance();
        } catch (final InstantiationException e) {
            throw new CoopieException(e);
        } catch (final IllegalAccessException e) {
            throw new CoopieException(e);
        }
    }

    @Override
    public Iterable<PropertyDesc<BEAN, ?>> propertyDescs() {
        return properties_.values();
    }

    @Override
    public int getPropertyDescSize() {
        return properties_.size();
    }

    @Override
    public Method getMethod(final String methodName) {
        return methods_.get(methodName);
    }

    private void setMethods(final BeanMethods methods) {
        methods_ = methods;
    }

    private void addPropertyDesc(final PropertyDesc<BEAN, ?> propertyDesc) {
        final String key = propertyName(propertyDesc.getPropertyName());
        properties_.put(key, propertyDesc);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<BEAN> {

        private Class<BEAN> beanClass_;
        private final Map<String, DefaultPropertyDesc.Builder<BEAN, ?>> properties_ = CollectionsUtil.newHashMap();

        public Builder<BEAN> beanClass(final Class<BEAN> clazz) {
            beanClass_ = clazz;
            return this;
        }

        public DefaultBeanDesc<BEAN> build() {
            setupPropertyDescs();
            final DefaultBeanDesc<BEAN> beanDesc = new DefaultBeanDesc<>();
            beanDesc.setBeanClass(beanClass_);
            for (final DefaultPropertyDesc.Builder<BEAN, ?> propertyBuilder : properties_.values()) {
                propertyBuilder.beanDesc(beanDesc);
                if (propertyBuilder.isValid()) {
                    final DefaultPropertyDesc<BEAN, ?> pd = propertyBuilder.build();
                    beanDesc.addPropertyDesc(pd);
                }
            }
            final BeanMethods methods = BeanMethods.builder().build(beanClass_);
            beanDesc.setMethods(methods);
            return beanDesc;
        }

        private void setupPropertyDescs() {
            for (final Method m : beanClass_.getMethods()) {
                if (m.isBridge() || m.isSynthetic()) {
                    continue;
                }
                final String methodName = m.getName();
                if (methodName.startsWith("get")) {
                    if (m.getParameterTypes().length != 0 || methodName.equals("getClass")
                            || m.getReturnType() == void.class) {
                        continue;
                    }
                    final String propertyName = decapitalizePropertyName(methodName.substring(3));
                    setupReadMethod(m, propertyName);
                } else if (methodName.startsWith("is")) {
                    if (m.getParameterTypes().length != 0 || !m.getReturnType().equals(Boolean.TYPE)) {
                        continue;
                    }
                    final String propertyName = decapitalizePropertyName(methodName.substring(2));
                    setupReadMethod(m, propertyName);
                } else if (methodName.startsWith("set")) {
                    if (m.getParameterTypes().length != 1 || methodName.equals("setClass")
                            || m.getReturnType() != void.class) {
                        continue;
                    }
                    final String propertyName = decapitalizePropertyName(methodName.substring(3));
                    setupWriteMethod(m, propertyName);
                }
            }
        }

        private String decapitalizePropertyName(final String name) {
            if (Text.isEmpty(name)) {
                return name;
            }

            if (2 <= name.length() && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
                return name;
            }
            final char[] chars = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }

        private void setupReadMethod(final Method readMethod, final String propertyName) {
            final DefaultPropertyDesc.Builder<BEAN, ?> builder = getPropertyBuilder(propertyName);
            builder.readMethod(readMethod);
        }

        private void setupWriteMethod(final Method writeMethod, final String propertyName) {
            final DefaultPropertyDesc.Builder<BEAN, ?> builder = getPropertyBuilder(propertyName);
            builder.writeMethod(writeMethod);
        }

        private DefaultPropertyDesc.Builder<BEAN, ?> getPropertyBuilder(final String propertyName) {
            DefaultPropertyDesc.Builder<BEAN, ?> builder = properties_.get(propertyName);
            if (builder == null) {
                builder = DefaultPropertyDesc.builder();
                builder.propertyName(propertyName);
                properties_.put(propertyName, builder);
            }
            return builder;
        }

    }

    private static class BeanMethods {
        private final Map<String, List<Method>> methods_;

        private BeanMethods(final Map<String, List<Method>> methods) {
            methods_ = methods;
        }

        static Builder builder() {
            return new Builder();
        }

        public Method get(final String methodName) {
            final List<Method> methods = methods_.get(methodName);
            if (methods != null) {
                return methods.get(0);
            }
            return null;
        }

        private static class Builder {
            BeanMethods build(final Class<?> beanClass) {
                final Map<String, List<Method>> methods = CollectionsUtil.newHashMap();

                for (final Method m : beanClass.getMethods()) {
                    if (m.isBridge() || m.isSynthetic()) {
                        continue;
                    }

                    final String methodName = m.getName();
                    List<Method> list = methods.get(methodName);
                    if (list == null) {
                        list = new ArrayList<>();
                        methods.put(methodName, list);
                    }
                    list.add(m);
                }
                return new BeanMethods(methods);
            }
        }

    }

}
