package jp.sourceforge.hotchpotch.coopie;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.task.Task;

public class ClosingGuardianTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void unclosed() throws Throwable {
        // ## Arrange ##
        runGC();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new Task<Void, RuntimeException>() {
            @Override
            public Void execute() throws RuntimeException {
                Aaa a = new Aaa();
                a = null;

                // さすがに1000回チャンスがあればGCされるでしょう
                for (int i = 0; i < 1000 && baos.size() == 0; i++) {
                    runGC();
                }
                return null;
            }
        });

        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);

        // ## Assert ##
        assertThat(ret, containsString("closed at finalize"));
        assertThat(ret, containsString(ClosingGuardian.STACKTRACE_TEXT));
        assertThat(ret, not(containsString(ClosingGuardian.SUCCESS_TEXT)));
    }

    @Test
    public void closed() throws Throwable {
        // ## Arrange ##
        runGC();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new Task<Void, IOException>() {
            @Override
            public Void execute() throws IOException {
                Aaa a = new Aaa();
                a.close();
                a = null;

                // さすがに1000回チャンスがあればGCされるでしょう
                for (int i = 0; i < 1000 && baos.size() == 0; i++) {
                    runGC();
                }
                return null;
            }
        });

        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);

        // ## Assert ##
        if (ret.contains("closed at finalize")) {
            fail(ret);
        }
        assertThat(ret, not(containsString("closed at finalize")));
        assertThat(ret, not(containsString(ClosingGuardian.STACKTRACE_TEXT)));
        assertThat(ret, containsString(ClosingGuardian.SUCCESS_TEXT));
    }

    private static class Aaa implements Closable {

        private boolean closed;

        @SuppressWarnings("unused")
        private final Object finalizerGuardian = new ClosingGuardian(this);

        @Override
        public void close() throws IOException {
            closed = true;
        }

        @Override
        public boolean isClosed() {
            return closed;
        }

    }

    private void runGC() {
        final Runtime runtime = Runtime.getRuntime();
        for (int i = 0; i < 5; i++) {
            runtime.runFinalization();
            runtime.gc();
        }
    }

}
