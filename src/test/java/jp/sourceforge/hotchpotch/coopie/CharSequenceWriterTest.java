package jp.sourceforge.hotchpotch.coopie;

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
        assertEquals("abb" + writer.getLineSeparator() + "ccc",
                writer.toString());
        assertEquals(false, "abbccc".equals(writer.toString()));
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
    }

}
