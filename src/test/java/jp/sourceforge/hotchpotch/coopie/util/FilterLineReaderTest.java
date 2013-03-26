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

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

public class FilterLineReaderTest {

    /**
     * 不要行を除く。
     * 
     * ここでは空行を除いている。
     */
    @Test
    public void filter1() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("\r\n" + "a1\r\n" + "a2\n" + "\r\n"
                + "\r\n" + "a3" + "\n" + "\r");

        // ## Act ##
        final LineFilter filter = new SkipLineFilter();
        final LineReader rr = new FilterLineReader(r, filter);

        // ## Assert ##
        assertEquals("a1", rr.readLine().getBody());
        assertEquals(2, rr.getLineNumber());
        assertEquals("a2", rr.readLine().getBody());
        assertEquals(3, rr.getLineNumber());
        assertEquals("a3", rr.readLine().getBody());
        assertEquals(6, rr.getLineNumber());
        assertEquals(null, rr.readLine());
        rr.close();
    }

    @Test
    public void filter2() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("\r\n" + "a1\r\n" + "a2\n" + "\r\n"
                + "\r\n" + "a3" + "\n" + "\r");

        // ## Act ##
        final Line line = new LineImpl();
        final LineFilter filter = new SkipLineFilter();
        final LineReader rr = new FilterLineReader(r, filter);

        // ## Assert ##
        assertEquals("a1", rr.readLine(line).getBody());
        assertEquals(2, rr.getLineNumber());
        assertEquals("a2", rr.readLine(line).getBody());
        assertEquals(3, rr.getLineNumber());
        assertEquals("a3", rr.readLine(line).getBody());
        assertEquals(6, rr.getLineNumber());
        assertEquals(null, rr.readLine(line));
        rr.close();
    }

    private LineReadable create(final String in) {
        final Readable readable = new StringReader(in);
        return new LineReadable(readable);
    }

    private static class SkipLineFilter implements LineFilter {
        @Override
        public boolean accept(final Line line) {
            if ("".equals(line.getBody())) {
                return false;
            }
            return true;
        }
    }

}
