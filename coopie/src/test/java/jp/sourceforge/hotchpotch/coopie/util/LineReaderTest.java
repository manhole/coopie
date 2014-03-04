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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

public class LineReaderTest {

    @Test
    public void readLineBody1() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("0123456789");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals("0123456789", line);
        assertEquals(1, r.getLineNumber());
        assertEquals(LineSeparator.NONE, r.getLineSeparator());

        assertEquals(null, r.readLineBody());
        assertEquals(1, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLineBody2() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("0123456789\r\n");
        assertEquals(0, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals("0123456789", line);
        assertEquals(1, r.getLineNumber());
        assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        assertEquals(null, r.readLineBody());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLineBody3() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        {
            final String line = r.readLineBody();
            assertEquals(1, r.getLineNumber());
            assertEquals("0123", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(2, r.getLineNumber());
            assertEquals("45678", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(3, r.getLineNumber());
            assertEquals("", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(4, r.getLineNumber());
            assertEquals("9", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }

        assertEquals(null, r.readLineBody());
        assertEquals(4, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLine3_1() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        final Line line = new LineImpl();
        {
            assertSame(line, r.readLine(line));
            assertEquals(1, line.getNumber());
            assertEquals("0123", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            assertSame(line, r.readLine(line));
            assertEquals(2, line.getNumber());
            assertEquals("45678", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            assertSame(line, r.readLine(line));
            assertEquals(3, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            assertSame(line, r.readLine(line));
            assertEquals(4, line.getNumber());
            assertEquals("9", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }

        assertEquals(null, r.readLine(line));
        assertEquals(4, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLine3_2() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(1, line.getNumber());
            assertEquals("0123", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(2, line.getNumber());
            assertEquals("45678", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(3, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(4, line.getNumber());
            assertEquals("9", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }

        final Line line = r.readLine();
        assertNull(line);
        assertEquals(4, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLineBody4() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("\n0123\n\n456\r789");

        // ## Act ##
        // ## Assert ##

        {
            final String line = r.readLineBody();
            assertEquals(1, r.getLineNumber());
            assertEquals("", line);
            assertEquals(LineSeparator.LF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(2, r.getLineNumber());
            assertEquals("0123", line);
            assertEquals(LineSeparator.LF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(3, r.getLineNumber());
            assertEquals("", line);
            assertEquals(LineSeparator.LF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(4, r.getLineNumber());
            assertEquals("456", line);
            assertEquals(LineSeparator.CR, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(5, r.getLineNumber());
            assertEquals("789", line);
            assertEquals(LineSeparator.NONE, r.getLineSeparator());
        }

        assertEquals(null, r.readLineBody());
        assertEquals(5, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    /*
     * CRとLFの間でbufferが切り替わる場合
     */
    @Test
    public void readLineBody5() throws Throwable {
        // ## Arrange ##
        final Readable readable = new StringReader("012345\r\n6789");
        final LineReader r = new LineReader(readable, 7);

        // ## Act ##
        // ## Assert ##
        {
            final String line = r.readLineBody();
            assertEquals("012345", line);
            assertEquals(1, r.getLineNumber());
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals("6789", line);
            assertEquals(2, r.getLineNumber());
            assertEquals(LineSeparator.NONE, r.getLineSeparator());
        }

        r.close();
    }

    /*
     * 空文字
     * 
     * BufferedReaderの振る舞いに合わせる。
     */
    @Test
    public void readLineBody_empty() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals(null, line);
        assertEquals(0, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        assertEquals(null, r.readLineBody());
        assertEquals(0, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    /*
     * スペースのみ
     */
    @Test
    public void readLineBody_space() throws Throwable {
        // ## Arrange ##
        final LineReader r = create(" ");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals(" ", line);
        assertEquals(1, r.getLineNumber());
        assertEquals(LineSeparator.NONE, r.getLineSeparator());

        assertEquals(null, r.readLineBody());
        assertEquals(1, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLine_endsWithCR() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("abc\r");

        // ## Act ##
        // ## Assert ##
        final Line line = new LineImpl();
        {
            assertSame(line, r.readLine(line));
            assertEquals(1, line.getNumber());
            assertEquals("abc", line.getBody());
            assertEquals(LineSeparator.CR, line.getSeparator());
        }

        assertEquals(null, r.readLine(line));
        assertEquals(1, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLine_endsWithLF() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("abc\n");

        // ## Act ##
        // ## Assert ##
        final Line line = new LineImpl();
        {
            assertSame(line, r.readLine(line));
            assertEquals(1, line.getNumber());
            assertEquals("abc", line.getBody());
            assertEquals(LineSeparator.LF, line.getSeparator());
        }

        assertEquals(null, r.readLine(line));
        assertEquals(1, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void pushback1() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("a1\r\n" + "a2\r" + "a3\n" + "a4\n" + "a5");

        // ## Act ##
        // ## Assert ##
        final Line l1 = r.readLine();
        assertEquals(1, r.getLineNumber());
        final Line l2 = r.readLine();
        assertEquals(2, r.getLineNumber());
        final Line l3 = r.readLine();
        assertEquals(3, r.getLineNumber());
        r.pushback(l3);
        r.pushback(l2);
        assertEquals(1, r.getLineNumber());

        assertEquals("a2", r.readLineBody());
        assertEquals(2, r.getLineNumber());
        assertEquals("a3", r.readLineBody());
        assertEquals(3, r.getLineNumber());
        assertEquals("a4", r.readLineBody());
        assertEquals(4, r.getLineNumber());
        assertEquals("a5", r.readLineBody());
        assertEquals(5, r.getLineNumber());
        assertEquals(null, r.readLineBody());

        r.close();
    }

    @Test
    public void pushback2() throws Throwable {
        // ## Arrange ##
        final LineReader r = create("a1\r\n" + "a2\r" + "a3\n" + "a4\n" + "a5");

        // ## Act ##
        // ## Assert ##
        final Line l1 = r.readLine();
        final Line l2 = r.readLine();
        final Line l3 = r.readLine();
        r.pushback(l1);
        r.pushback(l2);

        assertEquals(1, r.getLineNumber());
        assertEquals("a2", r.readLineBody());
        assertEquals(2, r.getLineNumber());
        assertEquals("a1", r.readLineBody());
        assertEquals(3, r.getLineNumber());
        assertEquals("a4", r.readLineBody());
        assertEquals(4, r.getLineNumber());
        assertEquals("a5", r.readLineBody());
        assertEquals(5, r.getLineNumber());
        assertEquals(null, r.readLineBody());

        r.close();
    }

    @Test
    public void iterateLine1() throws Throwable {
        // ## Arrange ##
        final LineReader r = create(LineSeparator.CR, "a1", "a2", "a3", "a4");

        // ## Act ##
        final Iterator<Line> it = r.iterator();

        // ## Assert ##
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getBody(), is("a1"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getBody(), is("a2"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getBody(), is("a3"));
        assertThat(it.hasNext(), is(true));
        assertThat(it.next().getBody(), is("a4"));
        assertThat(it.hasNext(), is(false));
        r.close();
    }

    @Test
    public void iterateLine2() throws Throwable {
        // ## Arrange ##
        final LineReader r = create(LineSeparator.CRLF, "a1", "a2", "a3");

        // ## Act ##
        final Iterator<Line> it = r.iterator();

        // ## Assert ##
        assertThat(it.next().getBody(), is("a1"));
        assertThat(it.next().getBody(), is("a2"));
        assertThat(it.next().getBody(), is("a3"));
        try {
            it.next();
            fail();
        } catch (final NoSuchElementException e) {
        }
        r.close();
    }

    @Test
    public void foreachLine1() throws Throwable {
        // ## Arrange ##
        final LineReader r = create(LineSeparator.CRLF, "a1", "a2", "a3");

        // ## Act ##
        // ## Assert ##
        final List<String> strs = new ArrayList<String>();
        for (final Line line : r) {
            strs.add(line.getBody());
        }
        assertThat(strs, is(Arrays.asList("a1", "a2", "a3")));
        assertThat(r.isClosed(), is(false));
        r.close();
    }

    private LineReader create(final String in) {
        final Readable readable = new StringReader(in);
        return new LineReader(readable);
    }

    private LineReader create(final LineSeparator sep, final String... lines) {
        final StringBuilder sb = new StringBuilder();
        for (final String line : lines) {
            sb.append(line);
            sb.append(sep.getSeparator());
        }
        return create(sb.toString());
    }

}
