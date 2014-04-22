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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class BufferedReadableTest {

    @Test
    public void test1() throws Throwable {
        final String in = "012345";
        _a1(create(in));
        _a1(create(in, 1));
        _a1(create(in, 2));
        _a1(create(in, 3));
        _a1(create(in, 4));
        _a1(create(in, 5));
        _a1(create(in, 6));
    }

    private void _a1(final BufferedReadable cs) throws IOException {
        assertEquals(false, cs.isEof());

        // ## Act ##
        // ## Assert ##
        assertEquals('0', cs.readChar());
        assertEquals('1', cs.readChar());
        assertEquals('2', cs.readChar());
        assertEquals('3', cs.readChar());
        assertEquals('4', cs.readChar());
        assertEquals('5', cs.readChar());
        assertEquals(false, cs.isEof());
        assertEquals(0, cs.readChar());
        assertEquals(0, cs.readChar());
        assertEquals(true, cs.isEof());

        cs.close();
    }

    @Test
    public void test2() throws Throwable {
        _a2(create("01234"));
        _a2(create("01234", 1));
        _a2(create("01234", 2));
        _a2(create("01234", 3));
        _a2(create("01234", 4));
        _a2(create("01234", 5));
    }

    private void _a2(final BufferedReadable cs) throws IOException {
        assertEquals(false, cs.isEof());

        assertEquals('0', cs.peekChar());
        assertEquals('0', cs.readChar());

        assertEquals('1', cs.peekChar());
        assertEquals('1', cs.readChar());

        assertEquals('2', cs.peekChar());
        assertEquals('2', cs.readChar());

        assertEquals('3', cs.peekChar());
        assertEquals('3', cs.peekChar());
        assertEquals('3', cs.readChar());

        assertEquals('4', cs.peekChar());
        assertEquals('4', cs.peekChar());
        assertEquals('4', cs.readChar());

        assertEquals(false, cs.isEof());
        assertEquals(0, cs.peekChar());
        assertEquals(0, cs.readChar());
        assertEquals(true, cs.isEof());

        cs.close();
    }

    @Test
    public void test3_1() throws Throwable {
        // ## Arrange ##
        final BufferedReadable cs = create("01234", 4);
        assertEquals(false, cs.isEof());

        // ## Act ##
        // ## Assert ##
        assertEquals('0', cs.peekChar());
        assertEquals('0', cs.readChar());

        assertEquals('1', cs.peekChar());
        assertEquals('1', cs.peekChar());
        assertEquals('1', cs.readChar());

        assertEquals('2', cs.readChar());

        assertEquals('3', cs.peekChar());
        assertEquals('3', cs.peekChar());
        assertEquals('3', cs.readChar());

        assertEquals('4', cs.peekChar());
        assertEquals('4', cs.peekChar());
        assertEquals('4', cs.readChar());
        assertEquals(false, cs.isEof());
        assertEquals(0, cs.readChar());
        assertEquals(0, cs.readChar());
        assertEquals(true, cs.isEof());

        cs.close();
    }

    @Test
    public void test3_2() throws Throwable {
        // ## Arrange ##
        final BufferedReadable cs = create("01234", 4);
        assertEquals(false, cs.isEof());

        // ## Act ##
        // ## Assert ##
        assertEquals('0', cs.peekChar());
        assertEquals('0', cs.readChar());

        assertEquals('1', cs.peekChar());
        assertEquals('1', cs.peekChar());
        assertEquals('1', cs.readChar());

        assertEquals('2', cs.readChar());

        assertEquals('3', cs.readChar());

        assertEquals('4', cs.readChar());
        assertEquals(false, cs.isEof());
        assertEquals(0, cs.readChar());
        assertEquals(0, cs.readChar());
        assertEquals(true, cs.isEof());

        cs.close();
    }

    @Test
    public void readChars1() throws Throwable {
        // ## Arrange ##
        final BufferedReadable cs = create("01234");

        // ## Act ##
        final char[] chars = cs.readChars();

        // ## Assert ##
        assertArrayEquals(new char[] { '0', '1', '2', '3', '4' }, chars);

        cs.close();
    }

    @Test
    public void readChars2() throws Throwable {
        // ## Arrange ##
        final BufferedReadable cs = create("01234", 3);

        // ## Act ##
        // ## Assert ##
        {
            final char[] chars = cs.readChars();
            assertArrayEquals(new char[] { '0', '1', '2' }, chars);
        }
        {
            final char[] chars = cs.readChars();
            assertArrayEquals(new char[] { '3', '4' }, chars);
        }
        {
            final char[] chars = cs.readChars();
            assertEquals(null, chars);
        }
        cs.close();
    }

    @Test
    public void readChars3() throws Throwable {
        // ## Arrange ##
        final BufferedReadable cs = create("01234", 3);

        // ## Act ##
        // ## Assert ##
        {
            final char c = cs.readChar();
            assertEquals('0', c);
        }
        {
            final char[] chars = cs.readChars();
            // 今はこういう実装
            assertArrayEquals(new char[] { '1', '2', }, chars);
        }
        {
            final char[] chars = cs.readChars();
            assertArrayEquals(new char[] { '3', '4' }, chars);
        }
        {
            final char[] chars = cs.readChars();
            assertEquals(null, chars);
        }
        cs.close();
    }

    @Test
    public void readChars4() throws Throwable {
        // ## Arrange ##
        final BufferedReadable cs = create("01234", 3);

        // ## Act ##
        // ## Assert ##
        {
            final char c = cs.peekChar();
            assertEquals('0', c);
        }
        {
            final char[] chars = cs.readChars();
            assertArrayEquals(new char[] { '0', '1', '2', }, chars);
        }
        {
            final char c = cs.peekChar();
            assertEquals('3', c);
        }
        {
            final char[] chars = cs.readChars();
            assertArrayEquals(new char[] { '3', '4' }, chars);
        }
        {
            final char[] chars = cs.readChars();
            assertEquals(null, chars);
        }
        cs.close();
    }

    private BufferedReadable create(final String in) {
        final StringReader readable = new StringReader(in);
        return new BufferedReadable(readable);
    }

    private BufferedReadable create(final String in, final int bufferSize) {
        final StringReader readable = new StringReader(in);
        return new BufferedReadable(readable, bufferSize);
    }

}
