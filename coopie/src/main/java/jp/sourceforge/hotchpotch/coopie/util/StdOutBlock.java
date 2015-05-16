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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

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

    public static <V> Text trapOut(final Task<V> task) {
        final Block a = new Block();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        a.out(baos);
        a.execute(task);
        final Text text = new Text(baos.toString());
        return text;
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
        public <V> V execute(final Task<V> task) {
            final PrintStream pOut = out_;
            final PrintStream pErr = err_;
            try {
                final V ret = trap(task, pOut, pErr);
                return ret;
            } finally {
                pOut.flush();
            }
        }

        private <V> V trap(final Task<V> task, final PrintStream pOut, final PrintStream pErr) {

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
