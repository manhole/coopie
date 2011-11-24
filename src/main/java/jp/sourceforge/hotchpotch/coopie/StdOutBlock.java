package jp.sourceforge.hotchpotch.coopie;

import java.io.OutputStream;
import java.io.PrintStream;

import org.t2framework.commons.util.task.Task;

/*
 * for purpose of testing
 */
public class StdOutBlock {

    public static Block out(final OutputStream os) {
        final Block a = new Block();
        a.out(os);
        return a;
    }

    public static Block err(final OutputStream os) {
        final Block a = new Block();
        a.err(os);
        return a;
    }

    private StdOutBlock() {
    }

    public static class Block implements TaskExecutable {

        private PrintStream out_ = System.out;
        private PrintStream err_ = System.err;

        public Block out(final OutputStream os) {
            out_ = new PrintStream(os, true);
            return this;
        }

        public Block err(final OutputStream os) {
            err_ = new PrintStream(os, true);
            return this;
        }

        @Override
        public <V, E extends Throwable> V execute(final Task<V, E> task)
                throws E {
            final PrintStream pOut = out_;
            final PrintStream pErr = err_;
            try {
                final V ret = trap(task, pOut, pErr);
                return ret;
            } finally {
                pOut.flush();
            }
        }

        private <V, E extends Throwable> V trap(final Task<V, E> task,
                final PrintStream pOut, final PrintStream pErr) throws E {
            final PrintStream defaultOut = System.out;
            final PrintStream defaultErr = System.err;
            System.setOut(pOut);
            try {
                System.setErr(pErr);
                try {
                    return task.execute();
                } finally {
                    System.setErr(defaultErr);
                }
            } finally {
                System.setOut(defaultOut);
            }
        }

    }

}
