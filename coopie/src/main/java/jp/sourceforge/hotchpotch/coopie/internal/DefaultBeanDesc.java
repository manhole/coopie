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
import java.util.function.Supplier;

import jp.sourceforge.hotchpotch.coopie.CoopieException;
import jp.sourceforge.hotchpotch.coopie.util.Text;

/**
 * @author manhole
 */
class DefaultBeanDesc<BEAN> implements BeanDesc<BEAN> {

    private final Class<BEAN> beanClass_;
    private final BeanProperties<BEAN> properties_;
    private final BeanMethods methods_;

    DefaultBeanDesc(final Class<BEAN> beanClass, final BeanProperties<BEAN> properties, final BeanMethods methods) {
        beanClass_ = beanClass;
        properties_ = properties;
        methods_ = methods;
    }

    @Override
    public Class<BEAN> getBeanClass() {
        return beanClass_;
    }

    @Override
    public boolean hasPropertyDesc(final String propertyName) {
        return properties_.hasProperty(propertyName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <PROPERTY> PropertyDesc<BEAN, PROPERTY> getPropertyDesc(final String propertyName) {
        return (PropertyDesc<BEAN, PROPERTY>) properties_.getProperty(propertyName);
    }

    @Override
    public BEAN newInstance() {
        try {
            return beanClass_.newInstance();
        } catch (final InstantiationException | IllegalAccessException e) {
            throw new CoopieException(e);
        }
    }

    @Override
    public Iterable<PropertyDesc<BEAN, ?>> propertyDescs() {
        return properties_.properties();
    }

    @Override
    public int getPropertyDescSize() {
        return properties_.size();
    }

    @Override
    public Method getMethod(final String methodName) {
        return methods_.get(methodName);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static class Builder<BEAN> {

        private Class<BEAN> beanClass_;

        public Builder<BEAN> beanClass(final Class<BEAN> clazz) {
            beanClass_ = clazz;
            return this;
        }

        DefaultBeanDesc<BEAN> build() {
            final BeanDescSupplier<BEAN> beanDescSupplier = new BeanDescSupplier<>();

            final BeanProperties<BEAN> properties = BeanProperties.<BEAN>builder().beanClass(beanClass_).beanDescSupplier(beanDescSupplier).build();
            final BeanMethods methods = BeanMethods.<BEAN>builder().beanClass(beanClass_).build();
            final DefaultBeanDesc<BEAN> beanDesc = new DefaultBeanDesc<>(beanClass_, properties, methods);

            beanDescSupplier.setBeanDesc(beanDesc);

            return beanDesc;
        }

    }

    private static class BeanMethods {
        private final Map<String, List<Method>> methods_;

        private BeanMethods(final Map<String, List<Method>> methods) {
            methods_ = methods;
        }

        static <BEAN> Builder<BEAN> builder() {
            return new Builder<>();
        }

        public Method get(final String methodName) {
            final List<Method> methods = methods_.get(methodName);
            if (methods != null) {
                return methods.get(0);
            }
            return null;
        }

        private static class Builder<BEAN> {
            private Class<BEAN> beanClass_;

            Builder<BEAN> beanClass(final Class<BEAN> beanClass) {
                beanClass_ = beanClass;
                return this;
            }

            BeanMethods build() {
                final Map<String, List<Method>> methods = CollectionsUtil.newHashMap();

                for (final Method m : beanClass_.getMethods()) {
                    if (m.isBridge() || m.isSynthetic()) {
                        continue;
                    }

                    final String methodName = m.getName();
                    final List<Method> list = methods.computeIfAbsent(methodName, k -> new ArrayList<>());
                    list.add(m);
                }
                return new BeanMethods(methods);
            }
        }

    }

    private static class BeanProperties<BEAN> {

        private final Map<String, PropertyDesc<BEAN, ?>> properties_;

        public BeanProperties(final Map<String, PropertyDesc<BEAN, ?>> properties) {
            properties_ = properties;
        }

        static <BEAN> Builder<BEAN> builder() {
            return new Builder<>();
        }

        public boolean hasProperty(final String propertyName) {
            final String n = propertyName(propertyName);
            return properties_.containsKey(n);
        }

        public PropertyDesc<BEAN, ?> getProperty(final String propertyName) {
            final String n = propertyName(propertyName);
            return properties_.get(n);
        }

        public int size() {
            return properties_.size();
        }

        public Iterable<PropertyDesc<BEAN, ?>> properties() {
            return properties_.values();
        }

        private static class Builder<BEAN> {
            private final Map<String, DefaultPropertyDesc.Builder<BEAN, ?>> properties_ = CollectionsUtil.newHashMap();
            private BeanDescSupplier<BEAN> beanDescSupplier_;
            private Class<BEAN> beanClass_;

            Builder<BEAN> beanClass(final Class<BEAN> beanClass) {
                beanClass_ = beanClass;
                return this;
            }

            Builder<BEAN> beanDescSupplier(final BeanDescSupplier<BEAN> supplier) {
                beanDescSupplier_ = supplier;
                return this;
            }

            BeanProperties<BEAN> build() {
                setupPropertyDescs();
                final Map<String, PropertyDesc<BEAN, ?>> properties = CollectionsUtil.newHashMap();
                for (final DefaultPropertyDesc.Builder<BEAN, ?> propertyBuilder : properties_.values()) {
                    if (propertyBuilder.isValid()) {
                        final DefaultPropertyDesc<BEAN, ?> pd = propertyBuilder.build();
                        final String propertyName = propertyName(pd.getPropertyName());
                        properties.put(propertyName, pd);
                    }
                }
                return new BeanProperties<>(properties);

            }

            private void setupPropertyDescs() {
                for (final Method m : beanClass_.getMethods()) {
                    if (m.isBridge() || m.isSynthetic()) {
                        continue;
                    }
                    final String methodName = m.getName();
                    if (methodName.startsWith("get")) {
                        if (m.getParameterTypes().length != 0 || methodName.equals("getClass") || m.getReturnType() == void.class) {
                            continue;
                        }
                        final String propertyName = methodName.substring(3);
                        setupReadMethod(m, propertyName);
                    } else if (methodName.startsWith("is")) {
                        if (m.getParameterTypes().length != 0 || !m.getReturnType().equals(Boolean.TYPE)) {
                            continue;
                        }
                        final String propertyName = methodName.substring(2);
                        setupReadMethod(m, propertyName);
                    } else if (methodName.startsWith("set")) {
                        if (m.getParameterTypes().length != 1 || methodName.equals("setClass") || m.getReturnType() != void.class) {
                            continue;
                        }
                        final String propertyName = methodName.substring(3);
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
                final String name = decapitalizePropertyName(propertyName);
                return properties_.computeIfAbsent(name, k -> {
                    final DefaultPropertyDesc.Builder<BEAN, ?> builder = DefaultPropertyDesc.builder();
                    builder.propertyName(name);
                    builder.beanDescSupplier(beanDescSupplier_);
                    return builder;
                });
            }

        }

        private static String propertyName(final String propertyName) {
            if (propertyName == null) {
                return null;
            }
            return propertyName.toLowerCase();
        }

    }

    private static class BeanDescSupplier<BEAN> implements Supplier<BeanDesc<BEAN>> {

        private BeanDesc<BEAN> beanDesc_;

        public void setBeanDesc(final BeanDesc<BEAN> beanDesc) {
            beanDesc_ = beanDesc;
        }

        @Override
        public BeanDesc<BEAN> get() {
            return beanDesc_;
        }

    }


}
