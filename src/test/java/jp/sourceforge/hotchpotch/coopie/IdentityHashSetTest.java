package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class IdentityHashSetTest {

    private final SetSpec spec = new SetSpec();

    @Test
    public void add() throws Throwable {
        spec.testAdd(new HashSet<Object>());
        spec.testAdd(new IdentityHashSet<Object>());
    }

    @Test
    public void isEmpty() throws Throwable {
        spec.testIsEmpty(new HashSet<Object>());
        spec.testIsEmpty(new IdentityHashSet<Object>());
    }

    @Test
    public void remove() throws Throwable {
        spec.testRemove(new HashSet<Object>());
        spec.testRemove(new IdentityHashSet<Object>());
    }

    @Test
    public void clear() throws Throwable {
        spec.testClear(new HashSet<Object>());
        spec.testClear(new IdentityHashSet<Object>());
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
