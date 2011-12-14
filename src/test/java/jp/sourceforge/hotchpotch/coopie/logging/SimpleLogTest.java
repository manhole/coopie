package jp.sourceforge.hotchpotch.coopie.logging;

import static jp.sourceforge.hotchpotch.coopie.VarArgs.a;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import jp.sourceforge.hotchpotch.coopie.StdOutBlock;
import jp.sourceforge.hotchpotch.coopie.Text;

import org.junit.Test;
import org.t2framework.commons.util.task.EasyTask;

public class SimpleLogTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void debug1() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new SimpleLog(null, null));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("null"));
    }

    @Test
    public void debug2() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new SimpleLog("foo", null));
                return null;
            }
        });
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo"));
    }

    @Test
    public void debug3() {
        // ## Arrange ##
        // ## Act ##
        final String ret = execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                final Logger logger = LoggerFactory.getLogger();
                logger.debug(new SimpleLog("foo {}, {}", a("bar", "baz")));
                return null;
            }
        });

        // ## Assert ##
        logger.debug("[[{}]]", ret);
        assertThat(ret, containsString("foo bar, baz"));
    }

    protected String execute(final EasyTask<Void> task) {
        final Text text = StdOutBlock.trapOut(task);
        return text.toString();
    }

}
