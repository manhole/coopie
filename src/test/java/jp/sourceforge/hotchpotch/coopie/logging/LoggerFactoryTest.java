package jp.sourceforge.hotchpotch.coopie.logging;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import jp.sourceforge.hotchpotch.coopie.util.StdOutBlock;
import jp.sourceforge.hotchpotch.coopie.util.Text;

import org.junit.Test;
import org.t2framework.commons.util.task.EasyTask;

public class LoggerFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void debug1() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug("hogehoge");
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("hogehoge"));
    }

    @Test
    public void debug2() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug("hello {}.", "world");
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("hello world."));
    }

    @Test
    public void debug_null() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug((String) null);
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug_log_null1() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug((Log) null);
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug_log_null2() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new AaaLog(null, null));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug_log1() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new AaaLog("fooo", null));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("fooo"));
    }

    @Test
    public void debug_log2() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new AaaLog("foo {}, {}", a("bar", "baz")));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo bar, baz"));
    }

    @Test
    public void warn_log1() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.warn(new AaaLog("foooo", null));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foooo"));
    }

    @Test
    public void warn_log2() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.warn(new AaaLog("foo {}, {}", a("bar", "baz")));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo bar, baz"));
    }

    private String execute(final EasyTask<Void> task) {
        final Text text = StdOutBlock.trapOut(task);
        return text.toString();
    }

    private static class AaaLog implements Log {

        private final Object[] args_;
        private final String format_;

        public AaaLog(final String format, final Object[] args) {
            format_ = format;
            args_ = args;
        }

        @Override
        public String getFormat() {
            return format_;
        }

        @Override
        public Object[] getArgs() {
            return args_;
        }
    }

}
