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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

public class ContextClassLoaderBlockTest {

    /**
     * 指定したClassLoaderを、ContextClassLoaderにセットすること。
     */
    @Test
    public void test() throws Throwable {
        // ## Arrange ##
        final Object returns = new Object();
        final AtomicBoolean called = new AtomicBoolean(false);
        final ClassLoader cl = new ClassLoader() {
        };
        assertNotSame(cl, Thread.currentThread().getContextClassLoader());

        // ## Act ##
        final Object ret = ContextClassLoaderBlock.with(cl).execute(() -> {
            called.set(true);
            assertSame(cl, Thread.currentThread().getContextClassLoader());
            return returns;
        });

        // ## Assert ##
        assertNotSame(cl, Thread.currentThread().getContextClassLoader());
        assertSame(returns, ret);
        assertEquals(true, called.get());
    }

}
