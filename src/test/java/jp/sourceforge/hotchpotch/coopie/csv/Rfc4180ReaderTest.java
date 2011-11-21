package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.junit.Test;

public class Rfc4180ReaderTest {

    private final CsvAssert csvAssert_ = new CsvAssert();

    static final String CR = "\r";
    static final String LF = "\n";
    static final String CRLF = CR + LF;

    /*
     * ＜RFC＞
     * 
     * 各レコードは、改行(CRLF)を区切りとする、分割された行に配置される
     * 
     * aaa,bbb,ccc CRLF
     * zzz,yyy,xxx CRLF
     */
    @Test
    public void rfc1() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aaa,bbb,ccc" + CRLF + "zzz,yyy,xxx"
                + CRLF);

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertEquals(1, reader.getRecordNo());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertEquals(2, reader.getRecordNo());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC＞
     * 
     * 末端のCRLFはあってもなくても良い
     * 
     * aaa,bbb,ccc CRLF
     * zzz,yyy,xxx
     */
    @Test
    public void rfc2() throws Throwable {
        // ## Arrange ##
        //final InputStream is = getResourceAsStream("-1", "tsv");

        final Rfc4180Reader reader = open("aaa,bbb,ccc" + CRLF + "zzz,yyy,xxx");

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertEquals(1, reader.getRecordNo());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertEquals(2, reader.getRecordNo());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC＞
     * 
     * スペースはデータの一部。無視してはいけない。
     * 
     * aaa,bbb , ccc
     */
    @Test
    public void rfc3() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(" aaa,bbb , ccc  ");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a(" aaa", "bbb ", " ccc  "), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC＞
     * 
     * 各フィールドは、それぞれダブルクォーテーションで囲んでも囲わなくてもよい
     * 
     * "aaa","bbb","ccc" CRLF
     * 123,yyy,xxx
     */
    @Test
    public void rfc4() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"bbb\",\"ccc\"" + CRLF
                + "123,yyy,xxx");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("123", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC＞
     * 
     * 改行(CRLF)、ダブルクォーテーション、カンマを含むフィールドは、ダブルクォーテーションで囲むべき
     * 
     * "aaa","b CRLF
     * bb","ccc" CRLF
     * 123,yyy,xxx
     */
    @Test
    public void rfc5_crlf() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"b" + CRLF + "bb\",\"ccc\""
                + CRLF + "123,yyy,xxx");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "b\r\nbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("123", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC＞
     * 
     * 改行(CRLF)、ダブルクォーテーション、カンマを含むフィールドは、ダブルクォーテーションで囲むべき
     * 
     * "aa,a","b CRLF
     * bb","ccc" CRLF
     * 123,yyy,xxx
     */
    @Test
    public void rfc6_comma() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aa,a\",\"b" + CRLF
                + "bb\",\"ccc\"" + CRLF + "123,yyy,xxx");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aa,a", "b\r\nbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("123", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC＞
     * 
     * フィールドがダブルクォーテーションで囲まれている場合、フィールドの値に含まれるダブルクォーテーションは、
     * その直前にひとつダブルクォーテーションを付加して、エスケープしなければならない
     * 
     * "aaa","b""bb","ccc"
     */
    @Test
    public void rfc7_quote() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"b\"\"bb\",\"ccc\"");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "b\"bb", "ccc"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC拡張＞
     * 
     * 改行文字がCRLFではなくLFでも、同じように読めるようにする。
     */
    @Test
    public void test_LF() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aaa,bbb,ccc" + LF + "zzz,yyy,xxx"
                + LF);

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC拡張＞
     * 
     * 改行文字がCRLFではなくCRでも、同じように読めるようにする。
     */
    @Test
    public void test_CR() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aaa,bbb,ccc" + CR + "zzz,yyy,xxx"
                + CR);

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * ＜RFC拡張＞
     * 
     * 要素の区切り文字に、カンマではなくTABを使用できる。
     * 
     * aaa TAB bbb TAB ccc CRLF
     * zzz TAB yyy TAB xxx CRLF
     */
    @Test
    public void tab1() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aaa\tbbb\tccc" + CRLF
                + "zzz\tyyy\txxx" + CRLF, CsvSetting.TAB);

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
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
        final Rfc4180Reader reader = open("aaa\tbb,b\tccc" + CRLF,
                CsvSetting.TAB);

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("aaa", "bb,b", "ccc"), reader.readRecord());
        assertNull(reader.readRecord());
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
        final Rfc4180Reader reader = open("aaa\t\"bb\tb\"\tccc" + CRLF,
                CsvSetting.TAB);

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("aaa", "bb\tb", "ccc"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * データ中にnull文字があっても、扱えること。
     */
    @Test
    public void null_char() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aa" + '\u0000' + "a,bbb");

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("aa" + '\u0000' + "a", "bbb"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * データ中の空要素は""として読むこと。
     */
    @Test
    public void empty_element() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(",aa,, ,   ,bbb,");

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("", "aa", "", " ", "   ", "bbb", ""),
                reader.readRecord());
        assertNull(reader.readRecord());
    }

    @Test
    public void quotechar_single() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = new Rfc4180Reader();
        reader.open(new StringReader("'a','b'" + CRLF));
        reader.setQuoteMark('\'');

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("a", "b"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    @Test
    public void only_quote_record() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("\""), reader.readRecord());
        assertNull(reader.readRecord());
    }

    /*
     * 不正データ
     * 
     * クォートされた要素内で、EOFになった場合
     * 
     * 途切れた要素を、1要素として読み込む
     */
    @Test
    public void eof_inQuotedElement() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"bbb\",\"cc");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "bbb", "cc"), reader.readRecord());
        assertNull(reader.readRecord());
    }

    protected Rfc4180Reader open(final String text) {
        return open(text, null);
    }

    protected Rfc4180Reader open(final String text,
            final Character elementSeparator) {
        final StringReader reader = new StringReader(text);
        final Rfc4180Reader csvReader = new Rfc4180Reader();
        if (elementSeparator != null) {
            csvReader.setElementSeparator(elementSeparator);
        }
        csvReader.open(reader);
        return csvReader;
    }

    private void assertArrayEquals(final String[] expected,
            final String[] actual) {
        csvAssert_.assertArrayEquals(expected, actual);
    }

}