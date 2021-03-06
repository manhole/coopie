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
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;
import jp.sourceforge.hotchpotch.coopie.util.StdOutBlock;
import jp.sourceforge.hotchpotch.coopie.util.Text;

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
