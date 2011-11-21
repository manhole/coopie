package jp.sourceforge.hotchpotch.coopie.logging;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;

import jp.sourceforge.hotchpotch.coopie.util.StdOutBlock;

import org.junit.Test;
import org.t2framework.commons.util.task.EasyTask;

public class SimpleLogTest {

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
                logger.debug(new SimpleLog(null, null));
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
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
                logger.debug(new SimpleLog("foo", null));
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo"));
    }

    @Test
    public void debug3() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new SimpleLog("foo {}, {}", a("bar", "baz")));
                return null;
            }
        });

        // ## Assert ##
        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo bar, baz"));
    }

}
