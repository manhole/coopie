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

import java.util.Map;

public class MapPropertyBinding<PROP> implements
        PropertyBinding<Map<String, PROP>, PROP> {

    private final String name_;

    public MapPropertyBinding(final String name) {
        name_ = name;
    }

    @Override
    public void setValue(final Map<String, PROP> bean, final PROP value) {
        bean.put(name_, value);
    }

    @Override
    public PROP getValue(final Map<String, PROP> bean) {
        final PROP v = bean.get(name_);
        return v;
    }

    public static class Factory<PROP> implements
            PropertyBindingFactory<Map<String, PROP>> {

        private static Factory INSTANCE = new Factory();

        public static <PROP> Factory<PROP> getInstance() {
            return INSTANCE;
        }

        @Override
        public PropertyBinding<Map<String, PROP>, PROP> getPropertyBinding(
                final String name) {
            final MapPropertyBinding<PROP> pb = new MapPropertyBinding<PROP>(
                    name);
            return pb;
        }

    }

}
