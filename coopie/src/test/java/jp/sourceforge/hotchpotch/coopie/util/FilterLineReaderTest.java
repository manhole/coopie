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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
        final LineReader r = create("\r\n" + "a1\r\n" + "a2\n" + "\r\n"
                + "\r\n" + "a3" + "\n" + "\r");

        // ## Act ##
        final LineFilter filter = new SkipLineFilter();
        final LineReadable rr = new FilterLineReader(r, filter);

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
        final LineReader r = create("\r\n" + "a1\r\n" + "a2\n" + "\r\n"
                + "\r\n" + "a3" + "\n" + "\r");

        // ## Act ##
        final Line line = new LineImpl();
        final LineFilter filter = new SkipLineFilter();
        final LineReadable rr = new FilterLineReader(r, filter);

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

    @Test
    public void iterate() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("\r\n" + "a1\r\n" + "a2\n" + "\r\n"
                + "\r\n" + "a3" + "\n" + "\r");

        // ## Act ##
        final LineFilter filter = new SkipLineFilter();
        final LineReadable rr = new FilterLineReader(r, filter);

        // ## Assert ##
        int count = 0;
        for (final Line line : rr) {
            switch (count) {
            case 0:
                assertThat(line.getBody(), is("a1"));
                assertThat(line.getNumber(), is(2));
                break;
            case 1:
                assertThat(line.getBody(), is("a2"));
                assertThat(line.getNumber(), is(3));
                break;
            case 2:
                assertThat(line.getBody(), is("a3"));
                assertThat(line.getNumber(), is(6));
                break;
            default:
                fail();
                break;
            }
            count++;
        }
        assertThat(3, is(count));
    }

    private LineReader create(final String in) {
        final Readable readable = new StringReader(in);
        return new LineReader(readable);
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
