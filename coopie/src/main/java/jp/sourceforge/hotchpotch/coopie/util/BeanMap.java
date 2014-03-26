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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public class BeanMap extends AbstractMap<String, Object> {

    private final Object object_;
    private final BeanDesc<Object> beanDesc_;
    private final int propertyDescSize_;
    private final List<PropertyDesc<Object>> allPropertyDesc_;
    private boolean lenient_ = true;

    public BeanMap(final Object obj) {
        object_ = obj;
        final Class<? extends Object> clazz = obj.getClass();
        beanDesc_ = BeanDescFactory.getBeanDesc(clazz);
        propertyDescSize_ = beanDesc_.getPropertyDescSize();
        allPropertyDesc_ = beanDesc_.getAllPropertyDesc();
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
        final PropertyDesc<Object> pd = beanDesc_.getPropertyDesc(s);
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

    public String getKeyByIndex(final int index) {
        final PropertyDesc<Object> pd = allPropertyDesc_.get(index);
        return pd.getPropertyName();
    }

    public Object getValueByIndex(final int index) {
        final PropertyDesc<Object> pd = allPropertyDesc_.get(index);
        return pd.getValue(object_);
    }

    public Object getValue(final String key) {
        final PropertyDesc<Object> pd = beanDesc_.getPropertyDesc(key);
        if (pd == null) {
            throw new IllegalArgumentException("key [" + key + "] does not exist");
        }
        final Object v = pd.getValue(object_);
        return v;
    }

    @Override
    public Object put(final String key, final Object value) {
        final PropertyDesc<Object> pd = beanDesc_.getPropertyDesc(key);
        if (pd == null) {
            throw new IllegalArgumentException("key [" + key + "] does not exist");
        }
        assertTypeIfNeed(pd, value);
        final Object prev = pd.getValue(object_);
        pd.setValue(object_, value);
        return prev;
    }

    private void assertTypeIfNeed(final PropertyDesc<Object> pd, final Object value) {
        if (lenient_) {
            return;
        }
        if (value == null) {
            return;
        }

        final Class<?> expectedType = pd.getPropertyType();
        final Class<? extends Object> actualType = value.getClass();
        if (!expectedType.isAssignableFrom(actualType)) {
            throw new IllegalArgumentException("invalid type. expected:<" + expectedType.getName() + "> actual:<"
                    + actualType.getName() + ">");
        }
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

    protected Object getObject() {
        return object_;
    }

    protected List<PropertyDesc<Object>> getAllPropertyDesc() {
        return allPropertyDesc_;
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
            private final List<PropertyDesc<Object>> allPropertyDesc_;
            private int index_;

            EntryIterator(final BeanMap map) {
                map_ = map;
                allPropertyDesc_ = map_.getAllPropertyDesc();
            }

            @Override
            public boolean hasNext() {
                return index_ < allPropertyDesc_.size();
            }

            @Override
            public Map.Entry<String, Object> next() {
                final PropertyDesc<Object> pd = allPropertyDesc_.get(index_);
                index_++;
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

            private final BeanMap map_;
            private final List<PropertyDesc<Object>> allPropertyDesc_;
            private int index_;

            KeysIterator(final BeanMap map) {
                map_ = map;
                allPropertyDesc_ = map_.getAllPropertyDesc();
            }

            @Override
            public boolean hasNext() {
                return index_ < allPropertyDesc_.size();
            }

            @Override
            public String next() {
                final String value = map_.getKeyByIndex(index_);
                index_++;
                return value;
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

            private final BeanMap map_;
            private final List<PropertyDesc<Object>> allPropertyDesc_;
            private int index_;

            ValuesIterator(final BeanMap map) {
                map_ = map;
                allPropertyDesc_ = map_.getAllPropertyDesc();
            }

            @Override
            public boolean hasNext() {
                return index_ < allPropertyDesc_.size();
            }

            @Override
            public Object next() {
                final Object value = map_.getValueByIndex(index_);
                index_++;
                return value;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

        }

    }

}
