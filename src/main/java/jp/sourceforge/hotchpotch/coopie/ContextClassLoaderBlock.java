package jp.sourceforge.hotchpotch.coopie;

import org.t2framework.commons.util.task.Task;

public class ContextClassLoaderBlock<V, E extends Throwable> {

    private final ClassLoader classLoader;

    public static <V, E extends Throwable> ContextClassLoaderBlock<V, E> with(
            final ClassLoader classLoader) {
        return new ContextClassLoaderBlock<V, E>(classLoader);
    }

    private ContextClassLoaderBlock(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public V execute(final Task<V, E> task) throws E {
        final Thread thread = Thread.currentThread();
        final ClassLoader contextClassLoader = thread.getContextClassLoader();
        thread.setContextClassLoader(classLoader);
        try {
            final V v = task.execute();
            return v;
        } finally {
            thread.setContextClassLoader(contextClassLoader);
        }
    }

}
