package jp.sourceforge.hotchpotch.coopie;

import java.io.OutputStream;
import java.io.PrintStream;

import org.t2framework.commons.util.task.Task;

/*
 * for purpose of testing
 */
public class StdOutBlock {

    private PrintStream out = System.out;

    public void setOut(final OutputStream os) {
        out = new PrintStream(os, true);
    }

    public <V, E extends Throwable> V execute(final Task<V, E> task) throws E {
        final PrintStream ps = out;
        try {
            final V ret = trap(task, ps);
            return ret;
        } finally {
            ps.flush();
        }
    }

    private <V, E extends Throwable> V trap(final Task<V, E> task,
            final PrintStream printStream) throws E {
        final PrintStream defaultOut = System.out;
        System.setOut(printStream);
        try {
            return task.execute();
        } finally {
            System.setOut(defaultOut);
        }
    }

}
