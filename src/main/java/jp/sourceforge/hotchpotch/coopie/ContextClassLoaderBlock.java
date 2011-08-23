package jp.sourceforge.hotchpotch.coopie;

import org.t2framework.commons.util.task.Task;

public class ContextClassLoaderBlock implements TaskExecutable {

    private final ClassLoader classLoader;

    public static TaskExecutable with(final ClassLoader classLoader) {
        return new ContextClassLoaderBlock(classLoader);
    }

    private ContextClassLoaderBlock(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public <V, E extends Throwable> V execute(final Task<V, E> task) throws E {
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
