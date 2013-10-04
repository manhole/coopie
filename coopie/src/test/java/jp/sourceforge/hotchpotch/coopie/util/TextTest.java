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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TextTest {

    @Test
    public void trim1() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        assertEquals("abc", new Text(" abc ").trimWhitespace().toString());
        assertEquals("abc", new Text("abc ").trimWhitespace().toString());
        assertEquals("abc", new Text(" abc").trimWhitespace().toString());
        assertEquals("abc", new Text("abc").trimWhitespace().toString());
        assertEquals("abc", new Text(" 　abc　　").trimWhitespace().toString());
        assertEquals("abc　 def", new Text(" 　abc　 def　\r\n\t").trimWhitespace()
                .toString());
    }

    @Test
    public void trim2() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        // String相当
        assertEquals("abc　", new Text(" abc　 ").trim(Text.STANDARD).toString());
        // 全角スペースも対象
        assertEquals("abc", new Text(" abc　 ").trim(Text.WHITESPACE).toString());
    }

    @Test
    public void containsIgnoreLine() throws Throwable {
        // ## Arrange ##
        final Text text = new Text("　いつも格別のお引き立てを" + "\r\n" + "賜り誠にありがとう"
                + "\r\n" + "ございます。");

        // ## Act ##
        // ## Assert ##
        assertEquals(false, text.containsText("こんにちは"));
        assertEquals(true, text.containsText("いつも"));
        assertEquals(true, text.containsText("。"));
        assertEquals(true, text.containsText("お引き立てを賜り"));
        assertEquals(true, text.containsText("お引き立てを賜り誠にありがとうございます"));
    }

    @Test
    public void containsLine() throws Throwable {
        // ## Arrange ##
        final Text text = new Text("　いつも格別のお引き立てを" + "\r\n" + "賜り誠にありがとう"
                + "\r\n" + "ございます。");

        // ## Act ##
        // ## Assert ##
        assertEquals(false, text.containsLine("こんにちは"));
        assertEquals(true, text.containsLine("いつも"));
        assertEquals(true, text.containsLine("　いつも"));
        assertEquals(true, text.containsLine("。"));
        assertEquals(false, text.containsLine("お引き立てを賜り"));
        assertEquals(false, text.containsLine("お引き立てを賜り誠にありがとうございます"));
    }

    @Test
    public void toStringReturnsRawString() throws Throwable {
        final Text text = new Text("　カレーなどの料理に" + "\r\n" + "広く使うスパイスの" + "\r\n"
                + "対日価格が高騰している。");
        assertEquals("　カレーなどの料理に\r\n広く使うスパイスの\r\n対日価格が高騰している。", text.toString());
    }

    @Test
    public void deleteChar() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        assertEquals("", new Text("").deleteChar(',').toString());
        assertEquals("ab ", new Text("ab ").deleteChar(',').toString());
        assertEquals("ab ", new Text("a,b ").deleteChar(',').toString());
        assertEquals("abc", new Text(",ab,,c,").deleteChar(',').toString());
    }

    @Test
    public void getLine() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        {
            final Text text = new Text("1234");
            assertEquals(1, text.getLineSize());
            assertEquals("1234", text.getLine(0).getBody());
        }
        {
            final Text text = new Text("1234\n56");
            assertEquals(2, text.getLineSize());
            assertEquals("1234", text.getLine(0).getBody());
            assertEquals("56", text.getLine(1).getBody());
        }
        {
            final Text text = new Text("1234\n\n56");
            assertEquals(3, text.getLineSize());
            assertEquals("1234", text.getLine(0).getBody());
            assertEquals("", text.getLine(1).getBody());
            assertEquals("56", text.getLine(2).getBody());
        }
    }

    @Test
    public void getLineAsText() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        {
            final Text text = new Text("1234");
            assertEquals(1, text.getLineSize());
            assertEquals("1234", text.getLineAsText(0).toString());
        }
        {
            final Text text = new Text("1234\n56");
            assertEquals(2, text.getLineSize());
            assertEquals("1234", text.getLineAsText(0).toString());
            assertEquals("56", text.getLineAsText(1).toString());
        }
        {
            final Text text = new Text("1234\n\n56");
            assertEquals(3, text.getLineSize());
            assertEquals("1234", text.getLineAsText(0).toString());
            assertEquals("", text.getLineAsText(1).toString());
            assertEquals("56", text.getLineAsText(2).toString());
        }
    }

    @Test
    public void length() throws Throwable {
        {
            // この"𠮷"は補助文字
            final String s = "𠮷野家";
            assertEquals(4, s.length());
            assertEquals(3, Text.length(s));
            assertEquals(3, Text.length((CharSequence) s));
        }
        {
            final String s = "吉野家";
            assertEquals(3, s.length());
            assertEquals(3, Text.length(s));
            assertEquals(3, Text.length((CharSequence) s));
        }
        {
            final String s = "";
            assertEquals(0, s.length());
            assertEquals(0, Text.length(s));
            assertEquals(0, Text.length((CharSequence) s));
        }
        {
            final String s = null;
            assertEquals(0, Text.length(s));
            assertEquals(0, Text.length((CharSequence) s));
        }
    }

    @Test
    public void substring() throws Throwable {
        {
            // この"𠮷"は補助文字
            final String s = "𠮷野家";
            assertEquals("𠮷野", s.substring(0, 3));
            assertEquals("𠮷野", Text.substring(s, 0, 2));
            assertEquals("野家", Text.substring(s, 1, 3));
        }
    }

    @Test
    public void convertLineSeparator1() throws Throwable {
        final String in = "1234\n56\n\n78\n";
        final String expectedCrlf = "1234\r\n56\r\n\r\n78\r\n";
        final String expectedCr = "1234\r56\r\r78\r";

        final Text text = new Text(in);
        final Text converted1 = text.convertLineSeparator(LineSeparator.CRLF);
        assertThat(converted1.toString(), is(expectedCrlf));
        assertThat(converted1.toString(), is(not(in)));

        final Text converted2 = text.convertLineSeparator(LineSeparator.CR);
        assertThat(converted2.toString(), is(expectedCr));
        assertThat(converted2.toString(), is(not(in)));

        final Text converted3 = converted1
                .convertLineSeparator(LineSeparator.CR);
        assertThat(converted3.toString(), is(converted2.toString()));
    }

    @Test
    public void convertLineSeparator2() throws Throwable {
        final String in = "1234";

        final Text text = new Text(in);
        final Text converted1 = text.convertLineSeparator(LineSeparator.CRLF);
        assertThat(converted1.toString(), is(in));
    }

    @Test
    public void compactSpace1() throws Throwable {
        final String in = "1234\n56\r\n \n78\n";

        final Text text = new Text(in);
        final Text converted1 = text.compactSpace();
        assertThat(converted1.toString(), is("1234 56 78 "));
    }

    /*
     * 160(0xA0)はspace
     * 
     * ASCII 文字セット (128 ～ 255)
     * http://msdn.microsoft.com/ja-jp/library/cc392379.aspx
     * ASCII 文字セット (128 ～ 255) | VBScript関数リファレンス
     * http://www.kanaya440.com/contents/script/vbs/others/ascii2.html
     */
    @Test
    public void compactSpace2() throws Throwable {
        final char c = 160;
        final String in = "1234\n56\r\n" + c + " \n78\n";

        final Text text = new Text(in);
        final Text converted1 = text.compactSpace();
        assertThat(converted1.toString(), is("1234 56 78 "));
    }

    @Test
    public void compactSpace3() throws Throwable {
        final String in = "1234";

        final Text text = new Text(in);
        final Text converted1 = text.compactSpace();
        assertThat(converted1.toString(), is(in));
    }

}
