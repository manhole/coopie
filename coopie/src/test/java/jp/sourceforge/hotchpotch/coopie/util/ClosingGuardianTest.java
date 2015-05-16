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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;

public class ClosingGuardianTest {

    private static final Logger logger = LoggerFactory.getLogger();

    @Test
    public void unclosed() throws Throwable {
        // ## Arrange ##
        runGC();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        StdOutBlock.out(baos).execute(new Task<Void>() {
            @Override
            public Void execute() {
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
        StdOutBlock.out(baos).execute(new Task<Void>() {
            @Override
            public Void execute() {
                Aaa a = new Aaa();
                CloseableUtil.closeNoException(a);
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

        private boolean closed_;

        @SuppressWarnings("unused")
        private final Object finalizerGuardian_ = new ClosingGuardian(this);

        @Override
        public void close() throws IOException {
            closed_ = true;
        }

        @Override
        public boolean isClosed() {
            return closed_;
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
