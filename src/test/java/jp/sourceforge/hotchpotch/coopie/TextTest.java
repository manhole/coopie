package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextTest {

    @Test
    public void trim() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        assertEquals("abc", Text.trimWhitespace(" abc "));
        assertEquals("abc", Text.trimWhitespace("abc "));
        assertEquals("abc", Text.trimWhitespace(" abc"));
        assertEquals("abc", Text.trimWhitespace("abc"));
        assertEquals("abc", Text.trimWhitespace(" 　abc　　"));
        assertEquals("abc　 def", Text.trimWhitespace(" 　abc　 def　"));
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

}
