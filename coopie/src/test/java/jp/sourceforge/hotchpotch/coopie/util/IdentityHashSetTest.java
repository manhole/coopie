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

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class IdentityHashSetTest {

    private final SetSpec spec = new SetSpec();

    @Test
    public void add() throws Throwable {
        spec.testAdd(new HashSet<>());
        spec.testAdd(new IdentityHashSet<>());
    }

    @Test
    public void isEmpty() throws Throwable {
        spec.testIsEmpty(new HashSet<>());
        spec.testIsEmpty(new IdentityHashSet<>());
    }

    @Test
    public void remove() throws Throwable {
        spec.testRemove(new HashSet<>());
        spec.testRemove(new IdentityHashSet<>());
    }

    @Test
    public void clear() throws Throwable {
        spec.testClear(new HashSet<>());
        spec.testClear(new IdentityHashSet<>());
    }

    @SuppressWarnings("unchecked")
    static class SetSpec {

        public void testAdd(final Set set) {
            final Object item = new Object();
            assertEquals(true, set.add(item));
            assertEquals(false, set.add(item));
        }

        public void testIsEmpty(final Set set) {
            assertEquals(true, set.isEmpty());
            set.add("123");
            assertEquals(false, set.isEmpty());
        }

        public void testRemove(final Set set) throws Throwable {
            final Object item = new Object();
            assertEquals(false, set.remove(item));
            assertEquals(true, set.add(item));
            assertEquals(true, set.remove(item));
        }

        public void testClear(final Set set) throws Throwable {
            assertEquals(true, set.add(new Object()));
            assertEquals(false, set.isEmpty());
            assertEquals(1, set.size());

            assertEquals(true, set.add(new Object()));
            assertEquals(2, set.size());

            set.clear();

            assertEquals(0, set.size());
            assertEquals(true, set.isEmpty());
        }

    }

}
