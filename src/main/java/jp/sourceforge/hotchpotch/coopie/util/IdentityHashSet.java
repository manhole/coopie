package jp.sourceforge.hotchpotch.coopie.util;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class IdentityHashSet<E> implements Set<E> {

    private final Map<E, Object> map_ = new IdentityHashMap<E, Object>();
    private static final Object ITEM = new Object();

    @Override
    public boolean add(final E e) {
        final Object prev = map_.put(e, ITEM);
        return prev == null ? true : false;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void clear() {
        map_.clear();
    }

    @Override
    public boolean contains(final Object o) {
        return map_.containsKey(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean isEmpty() {
        return map_.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean remove(final Object o) {
        final Object removed = map_.remove(o);
        return removed != null ? true : false;
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int size() {
        return map_.size();
    }

    @Override
    public Object[] toArray() {
        throw new RuntimeException("not implemented");
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        throw new RuntimeException("not implemented");
    }

}
