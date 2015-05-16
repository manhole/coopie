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

package jp.sourceforge.hotchpotch.coopie.util;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.internal.BeanDesc;
import jp.sourceforge.hotchpotch.coopie.internal.BeanDescFactory;
import jp.sourceforge.hotchpotch.coopie.internal.PropertyDesc;

public class BeanMap extends AbstractMap<String, Object> {

    private final Object bean_;
    private final BeanDesc<Object> beanDesc_;
    private final int propertyDescSize_;
    private boolean lenient_ = false;

    @SuppressWarnings("unchecked")
    public BeanMap(final Object obj) {
        bean_ = obj;
        final Class<Object> clazz = (Class<Object>) obj.getClass();
        beanDesc_ = BeanDescFactory.getBeanDesc(clazz);
        propertyDescSize_ = beanDesc_.getPropertyDescSize();
    }

    @Override
    public int size() {
        return propertyDescSize_;
    }

    @Override
    public boolean isEmpty() {
        return 0 == propertyDescSize_;
    }

    @Override
    public boolean containsKey(final Object key) {
        final String s = key(key);
        final PropertyDesc<?, ?> pd = beanDesc_.getPropertyDesc(s);
        return pd != null;
    }

    @Override
    public boolean containsValue(final Object value) {
        return super.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
        final String s = key(key);
        final Object v = getValue(s);
        return v;
    }

    public Object getValue(final String key) {
        final PropertyDesc<Object, ?> pd = beanDesc_.getPropertyDesc(key);
        if (pd == null) {
            throw new IllegalArgumentException("key [" + key + "] does not exist");
        }
        final Object v = pd.getValue(bean_);
        return v;
    }

    @Override
    public Object put(final String key, final Object value) {
        final PropertyDesc<Object, Object> pd = beanDesc_.getPropertyDesc(key);
        if (pd == null) {
            throw new IllegalArgumentException("key [" + key + "] does not exist");
        }

        final Object valueToSet = assertTypeIfNeed(pd, value);
        final Object prev = pd.getValue(bean_);
        pd.setValue(bean_, valueToSet);
        return prev;
    }

    private Object assertTypeIfNeed(final PropertyDesc<Object, Object> pd, final Object value) {
        if (value == null) {
            return null;
        }

        final Class<?> expectedType = pd.getPropertyType();
        final Class<? extends Object> actualType = value.getClass();
        if (!expectedType.isAssignableFrom(actualType)) {
            if (lenient_) {
                final Object convertedValue = convertValueIfAvailable(value, expectedType, actualType);
                return convertedValue;
            }
            throw new IllegalArgumentException("invalid type. expected:<" + expectedType.getName() + "> actual:<"
                    + actualType.getName() + ">");
        }
        return value;
    }

    private Object convertValueIfAvailable(final Object value, final Class<?> expectedType, final Class<?> actualType) {
        if (Integer.class.isAssignableFrom(expectedType)) {
            return Integer.valueOf(value.toString());
        }
        return value;
    }

    @Override
    public Object remove(final Object key) {
        throw new UnsupportedOperationException("remove");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("clear");
    }

    @Override
    public Set<String> keySet() {
        return new BeanKeys(this);
    }

    @Override
    public Collection<Object> values() {
        return new BeanValues(this);
    }

    @Override
    public Set<Map.Entry<String, Object>> entrySet() {
        return new BeanEntrySet(this);
    }

    public void setLenient(final boolean lenient) {
        lenient_ = lenient;
    }

    protected Object getBean() {
        return bean_;
    }

    protected BeanDesc<Object> getBeanDesc() {
        return beanDesc_;
    }

    private String key(final Object key) {
        final String s = (String) key;
        return s;
    }

    private static class BeanEntrySet extends AbstractSet<Map.Entry<String, Object>> {

        private final BeanMap map_;

        BeanEntrySet(final BeanMap map) {
            map_ = map;
        }

        @Override
        public int size() {
            return map_.size();
        }

        @Override
        public boolean contains(final Object o) {
            return map_.containsKey(o);
        }

        @Override
        public Iterator<Map.Entry<String, Object>> iterator() {
            return new EntryIterator(map_);
        }

        @Override
        public boolean add(final Map.Entry<String, Object> e) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException("remove");
        }

        private static class EntryIterator implements Iterator<Map.Entry<String, Object>> {

            private final BeanMap map_;
            private final Iterator<PropertyDesc<Object, ?>> iterator_;

            EntryIterator(final BeanMap map) {
                map_ = map;
                iterator_ = map.getBeanDesc().propertyDescs().iterator();
            }

            @Override
            public boolean hasNext() {
                return iterator_.hasNext();
            }

            @Override
            public Map.Entry<String, Object> next() {
                final PropertyDesc<Object, ?> pd = iterator_.next();
                return new PropertyEntry(map_, pd.getPropertyName());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        }

        private static class PropertyEntry implements Map.Entry<String, Object> {

            private final BeanMap map_;
            private final String name_;

            PropertyEntry(final BeanMap map, final String name) {
                map_ = map;
                name_ = name;
            }

            @Override
            public String getKey() {
                return name_;
            }

            @Override
            public Object getValue() {
                return map_.getValue(name_);
            }

            @Override
            public Object setValue(final Object value) {
                throw new UnsupportedOperationException("setValue");
            }
        }

    }

    private static class BeanKeys extends AbstractSet<String> {

        private final BeanMap map_;

        BeanKeys(final BeanMap map) {
            map_ = map;
        }

        @Override
        public Iterator<String> iterator() {
            return new KeysIterator(map_);
        }

        @Override
        public int size() {
            return map_.size();
        }

        private static class KeysIterator implements Iterator<String> {

            private final Iterator<PropertyDesc<Object, ?>> iterator_;

            KeysIterator(final BeanMap map) {
                iterator_ = map.getBeanDesc().propertyDescs().iterator();
            }

            @Override
            public boolean hasNext() {
                return iterator_.hasNext();
            }

            @Override
            public String next() {
                final PropertyDesc<Object, ?> pd = iterator_.next();
                return pd.getPropertyName();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        }

    }

    private static class BeanValues extends AbstractCollection<Object> {

        private final BeanMap map_;

        BeanValues(final BeanMap map) {
            map_ = map;
        }

        @Override
        public Iterator<Object> iterator() {
            return new ValuesIterator(map_);
        }

        @Override
        public int size() {
            return map_.size();
        }

        private static class ValuesIterator implements Iterator<Object> {

            private final Iterator<PropertyDesc<Object, ?>> iterator_;
            private final Object bean_;

            ValuesIterator(final BeanMap map) {
                bean_ = map.getBean();
                iterator_ = map.getBeanDesc().propertyDescs().iterator();
            }

            @Override
            public boolean hasNext() {
                return iterator_.hasNext();
            }

            @Override
            public Object next() {
                final PropertyDesc<Object, ?> pd = iterator_.next();
                return pd.getValue(bean_);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        }

    }

}
