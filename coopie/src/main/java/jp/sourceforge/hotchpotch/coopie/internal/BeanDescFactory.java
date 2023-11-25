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

import java.util.Map;

/**
 * @author manhole
 */
public class BeanDescFactory {

    @SuppressWarnings("rawtypes")
    private static final Map<Class, BeanDesc> cache = CollectionsUtil.newHashMap();

    @SuppressWarnings("unchecked")
    public static <BEAN> BeanDesc<BEAN> getBeanDesc(final Class<BEAN> clazz) {
        BeanDesc<BEAN> beanDesc = cache.get(clazz);
        if (beanDesc == null) {
            beanDesc = create(clazz);
            cache.put(clazz, beanDesc);
        }
        return beanDesc;
    }

    public static void clear() {
        cache.clear();
    }

    private static <BEAN> BeanDesc<BEAN> create(final Class<BEAN> clazz) {
        return DefaultBeanDesc.<BEAN> builder().beanClass(clazz).build();
    }

}
