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

package jp.sourceforge.hotchpotch.coopie.util;

//import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.io.ByteArrayOutputStream;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.util.task.EasyTask;

public class StdOutBlockTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void testOut() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                System.out.println("to sysout");
                return null;
            }
        });

        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);

        // ## Assert ##
        assertThat(ret, containsString("to sysout"));
    }

    @Test
    public void testErr() {
        // ## Arrange ##
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.err(baos).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                System.err.println("to syserr");
                return null;
            }
        });

        final String ret = baos.toString();
        logger.debug("[[{}]]", ret);

        // ## Assert ##
        assertThat(ret, containsString("to syserr"));
    }

    @Test
    public void testOutErr() {
        // ## Arrange ##
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ByteArrayOutputStream err = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(out).err(err).execute(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                System.out.println("to sysout");
                System.err.println("to syserr");
                return null;
            }
        });

        final String retOut = out.toString();
        final String retErr = err.toString();
        logger.debug("[[{}]]", retOut);
        logger.debug("[[{}]]", retErr);

        // ## Assert ##
        assertThat(retOut, containsString("to sysout"));
        assertThat(retErr, containsString("to syserr"));

        assertThat(retOut, not(containsString("to syserr")));
        assertThat(retErr, not(containsString("to sysout")));
    }

    @Test
    public void testTrapOut() {
        // ## Arrange ##
        // ## Act ##
        final Text text = StdOutBlock.trapOut(new EasyTask<Void>() {
            @Override
            public Void execute() throws RuntimeException {
                System.out.println("trap sysout");
                return null;
            }
        });

        final String ret = text.toString();
        logger.debug("[[{}]]", ret);

        // ## Assert ##
        assertThat(ret, containsString("trap sysout"));
    }

}
