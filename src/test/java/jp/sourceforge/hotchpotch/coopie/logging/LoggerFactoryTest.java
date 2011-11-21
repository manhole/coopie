package jp.sourceforge.hotchpotch.coopie.logging;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;

import jp.sourceforge.hotchpotch.coopie.util.StdOutBlock;

import org.junit.Test;
import org.t2framework.commons.util.task.EasyTask;

public class LoggerFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void debug1() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug("hogehoge");
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("hogehoge"));
    }

    @Test
    public void debug2() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug("hello {}.", "world");
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("hello world."));
    }

    @Test
    public void debug_null() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug((String) null);
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug_log_null1() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug((Log) null);
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug_log_null2() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new AaaLog(null, null));
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug_log1() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new AaaLog("fooo", null));
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("fooo"));
    }

    @Test
    public void debug_log2() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new AaaLog("foo {}, {}", a("bar", "baz")));
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo bar, baz"));
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
