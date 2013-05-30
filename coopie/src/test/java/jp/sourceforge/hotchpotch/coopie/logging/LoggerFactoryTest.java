/*
 * Copyright 2010 manhole
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.logging;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import jp.sourceforge.hotchpotch.coopie.util.StdOutBlock;
import jp.sourceforge.hotchpotch.coopie.util.Text;

import org.junit.Test;
import org.t2framework.commons.util.task.EasyTask;

public class LoggerFactoryTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void loggerName() {
        assertThat(logger.getName(), is(LoggerFactoryTest.class.getName()));
    }

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
