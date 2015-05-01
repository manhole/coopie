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

public class ContextClassLoaderBlock implements TaskExecutable {

    private final ClassLoader classLoader_;

    public static TaskExecutable with(final ClassLoader classLoader) {
        return new ContextClassLoaderBlock(classLoader);
    }

    private ContextClassLoaderBlock(final ClassLoader classLoader) {
        classLoader_ = classLoader;
    }

    @Override
    public <V> V execute(final Task<V> task) {
        final Thread thread = Thread.currentThread();
        final ClassLoader contextClassLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(classLoader_);
        try {
            final V v = task.execute();
            return v;
        } finally {
            thread.setContextClassLoader(contextClassLoader);
        }
    }

}
