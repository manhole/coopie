package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.VarArgs.a;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.csv.Rfc4180Writer.QuoteMode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.t2framework.commons.util.ResourceUtil;

public class Rfc4180WriterTest {

    private final CsvAssert csvAssert_ = new CsvAssert();

    static final String CR = "\r";
    static final String LF = "\n";
    static final String CRLF = CR + LF;

    private StringWriter stringWriter_;

    @Before
    public void setUp() throws Throwable {
        stringWriter_ = new StringWriter();
    }

    @After
    public void tearDown() {
        stringWriter_ = null;
    }

    /*
     * 各レコードは、改行(CRLF)を区切りとする、分割された行に配置される
     * 
     * aaa,bbb,ccc CRLF
     * zzz,yyy,xxx CRLF
     */
    @Test
    public void rfc1() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("aaa", "bbb", "ccc"));
        writer.writeRecord(a("zzz", "yyy", "xxx"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa,bbb,ccc" + CRLF + "zzz,yyy,xxx" + CRLF, text);
    }

    /*
     * スペースはデータの一部。無視してはいけない。
     * 
     * aaa,bbb , ccc
     */
    @Test
    public void rfc3() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a(" aaa", "bbb ", " ccc  "));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals(" aaa,bbb , ccc  " + CRLF, text);
    }

    /*
     * 改行(CRLF)、ダブルクォーテーション、カンマを含むフィールドは、ダブルクォーテーションで囲むべき
     * 
     * aaa,"b CRLF
     * bb",ccc CRLF
     * 123,yyy,xxx
     */
    @Test
    public void rfc5_crlf() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("aaa", "b\r\nbb", "ccc"));
        writer.writeRecord(a("123", "yyy", "xxx"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa,\"b" + CRLF + "bb\",ccc" + CRLF + "123,yyy,xxx"
                + CRLF, text);
    }

    /*
     * 改行(CRLF)、ダブルクォーテーション、カンマを含むフィールドは、ダブルクォーテーションで囲むべき
     * 
     * "aa,a","b CRLF
     * bb",ccc CRLF
     * 123,yyy,xxx
     */
    @Test
    public void rfc6_comma() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("aa,a", "b\r\nbb", "ccc"));
        writer.writeRecord(a("123", "yyy", "xxx"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("\"aa,a\",\"b" + CRLF + "bb\",ccc" + CRLF + "123,yyy,xxx"
                + CRLF, text);
    }

    /*
     * フィールドがダブルクォーテーションで囲まれている場合、フィールドの値に含まれるダブルクォーテーションは、
     * その直前にひとつダブルクォーテーションを付加して、エスケープしなければならない
     * 
     * aaa,"b""bb",ccc
     */
    @Test
    public void rfc7_quote() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("aaa", "b\"bb", "ccc"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa,\"b\"\"bb\",ccc" + CRLF, text);
    }

    /*
     * ＜RFC拡張＞
     * 
     * 改行文字を、CRLFではなくLFで出力できるようにする。
     */
    @Test
    public void test_LF() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setLineSeparator(LF);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("aaa", "bbb", "ccc"));
        writer.writeRecord(a("zzz", "yyy", "xxx"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa,bbb,ccc" + LF + "zzz,yyy,xxx" + LF, text);
    }

    /*
     * ＜RFC拡張＞
     * 
     * 改行文字を、CRLFではなくCRで出力できるようにする。
     */
    @Test
    public void test_CR() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setLineSeparator(CR);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("aaa", "bbb", "ccc"));
        writer.writeRecord(a("zzz", "yyy", "xxx"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa,bbb,ccc" + CR + "zzz,yyy,xxx" + CR, text);
    }

    /*
     * ＜RFC拡張＞
     * 
     * 要素の区切り文字を、カンマではなくTABにも指定できる。
     * 
     * aaa TAB bbb TAB ccc CRLF
     * zzz TAB yyy TAB xxx CRLF
     */
    @Test
    public void tab1() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setElementSeparator(CsvSetting.TAB);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("aaa", "bbb", "ccc"));
        writer.writeRecord(a("zzz", "yyy", "xxx"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa\tbbb\tccc" + CRLF + "zzz\tyyy\txxx" + CRLF, text);
    }

    /*
     * ＜RFC拡張＞
     * 
     * 要素の区切り文字をTABに指定した場合は、要素のデータにカンマを使用できる。
     * 
     * aaa TAB bb,b TAB ccc
     */
    @Test
    public void tab2() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setElementSeparator(CsvSetting.TAB);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("aaa", "bb,b", "ccc"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa\tbb,b\tccc" + CRLF, text);
    }

    /*
     * ＜RFC拡張＞
     * 
     * 要素の区切り文字をTABに指定した場合に、要素のデータにTABを使用するには、
     * 要素をクォートする。
     * 
     * aaa TAB "bbTABb" TAB ccc
     */
    @Test
    public void tab3() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setElementSeparator(CsvSetting.TAB);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("aaa", "bb\tb", "ccc"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aaa\t\"bb\tb\"\tccc" + CRLF, text);
    }

    /*
     * ＜RFC拡張＞
     * 
     * 要素を常にクォートできるようにする。
     * 
     */
    @Test
    public void always_separator() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setQuoteMode(Rfc4180Writer.QuoteMode.ALWAYS);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("aaa", "bbb", "ccc"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("\"aaa\",\"bbb\",\"ccc\"" + CRLF, text);
    }

    /*
     * データ中にnull文字があっても、扱えること。
     */
    @Test
    public void null_char() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("aa" + '\u0000' + "a", "bbb"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("aa" + '\u0000' + "a,bbb" + CRLF, text);
    }

    /*
     * データ中の空要素は""にすること。
     * 
     * クォートがMINIMUMの場合
     */
    @Test
    public void empty_element_minimum() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("", "aa", "", " ", "   ", "bbb", ""));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals(",aa,, ,   ,bbb," + CRLF, text);
    }

    /*
     * データ中のnull要素は""にすること。
     * 
     * クォートがALWAYSの場合
     */
    @Test
    public void null_element_always() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setQuoteMode(Rfc4180Writer.QuoteMode.ALWAYS);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("", null, "   "));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("\"\",\"\",\"   \"" + CRLF, text);
    }

    /*
     * データ中のnull要素は""にすること。
     * 
     * クォートがALWAYS_EXCEPT_NULLの場合
     */
    @Test
    public void null_element_always_except_null() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setQuoteMode(Rfc4180Writer.QuoteMode.ALWAYS_EXCEPT_NULL);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("", null, "   "));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("\"\",,\"   \"" + CRLF, text);
    }

    /*
     * データ中のnull要素は""にすること。
     */
    @Test
    public void null_element_minimum() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = open();

        // ## Act ##
        writer.writeRecord(a("", null, "   "));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals(",,   " + CRLF, text);
    }

    @Test
    public void quotechar_single() throws Throwable {
        // ## Arrange ##
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setQuoteMark('\'');
        writer.setQuoteMode(QuoteMode.ALWAYS);
        writer.open(stringWriter_);

        // ## Act ##
        writer.writeRecord(a("a", "b"));
        writer.close();

        // ## Assert ##
        final String text = stringWriter_.toString();
        assertEquals("'a','b'" + CRLF, text);
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(
                Rfc4180WriterTest.class.getName() + suffix, ext);
    }

    protected Rfc4180Writer open() {
        final Rfc4180Writer csvWriter = new Rfc4180Writer();
        csvWriter.open(stringWriter_);
        return csvWriter;
    }

}
