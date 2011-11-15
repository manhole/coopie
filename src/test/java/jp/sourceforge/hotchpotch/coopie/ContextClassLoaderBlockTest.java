package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.t2framework.commons.util.task.Task;

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
        final Object ret = ContextClassLoaderBlock.with(cl).execute(
                new Task<Object, RuntimeException>() {
                    @Override
                    public Object execute() throws RuntimeException {
                        called.set(true);
                        assertSame(cl, Thread.currentThread()
                                .getContextClassLoader());
                        return returns;
                    }
                });

        // ## Assert ##
        assertNotSame(cl, Thread.currentThread().getContextClassLoader());
        assertSame(returns, ret);
        assertEquals(true, called.get());
    }

}
