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

import org.junit.Test;

public class CharSequenceWriterTest {

    /**
     * 何もwriteしない場合は、nullではなく空文字になる。
     */
    @Test
    public void empty() throws Throwable {
        // ## Arrange ##
        final CharSequenceWriter writer = new CharSequenceWriter();

        // ## Act ##

        // ## Assert ##
        assertEquals("", writer.toString());

        writer.close();
    }

    /**
     * Stringをwriteする。
     */
    @Test
    public void write1() throws Throwable {
        // ## Arrange ##
        final CharSequenceWriter writer = new CharSequenceWriter();

        // ## Act ##
        writer.write("a");
        writer.write("bb");
        writer.write("ccc");

        // ## Assert ##
        assertEquals("abbccc", writer.toString());

        writer.close();
    }

    /**
     * writeLineでseparatorが出力される。
     */
    @Test
    public void writeLine() throws Throwable {
        // ## Arrange ##
        final CharSequenceWriter writer = new CharSequenceWriter();

        // ## Act ##
        writer.write("a");
        writer.writeLine("bb");
        writer.write("ccc");

        // ## Assert ##
        assertEquals("abb" + writer.getLineSeparator() + "ccc", writer.toString());
        assertEquals(false, "abbccc".equals(writer.toString()));

        writer.close();
    }

    @Test
    public void custom_lineSeparator() throws Throwable {
        // ## Arrange ##
        final CharSequenceWriter writer = new CharSequenceWriter();
        writer.setLineSeparator("_");

        // ## Act ##
        writer.write("a");
        writer.writeLine("bb");
        writer.write("ccc");

        // ## Assert ##
        assertEquals("abb_ccc", writer.toString());

        writer.close();
    }

}
