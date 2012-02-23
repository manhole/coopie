package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

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
        assertEquals(0, reader.getRecordNumber());
        assertEquals(0, reader.getLineNumber());

        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertEquals(1, reader.getRecordNumber());
        assertEquals(1, reader.getLineNumber());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());

        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertEquals(2, reader.getRecordNumber());
        assertEquals(2, reader.getLineNumber());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());

        assertNull(reader.readRecord());
        assertEquals(2, reader.getRecordNumber());
        assertEquals(2, reader.getLineNumber());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());

        reader.close();
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
    public void rfc2_no_lastCRLF() throws Throwable {
        // ## Arrange ##
        //final InputStream is = getResourceAsStream("-1", "tsv");

        final Rfc4180Reader reader = open("aaa,bbb,ccc" + CRLF + "zzz,yyy,xxx");

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNumber());
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertEquals(1, reader.getRecordNumber());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertEquals(2, reader.getRecordNumber());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * ＜RFC＞
     * 
     * スペースはデータの一部。無視してはいけない。
     * 
     * aaa,bbb , ccc
     */
    @Test
    public void rfc3_space1() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(" aaa,bbb , ccc  ");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a(" aaa", "bbb ", " ccc  "), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    @Test
    public void rfc3_space2() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(" ,a ,");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a(" ", "a ", ""), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    @Test
    public void rfc3_space3() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(", a , ");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("", " a ", " "), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    @Test
    public void rfc3_space4() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(", a , \n");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("", " a ", " "), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
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
    public void rfc4_quote() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"bbb\",\"ccc\"" + CRLF
                + "123,yyy,xxx");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("123", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
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
        assertEquals(0, reader.getRecordNumber());
        assertEquals(0, reader.getLineNumber());

        assertArrayEquals(a("aaa", "b\r\nbb", "ccc"), reader.readRecord());
        assertEquals(1, reader.getRecordNumber());
        assertEquals(2, reader.getLineNumber());

        assertArrayEquals(a("123", "yyy", "xxx"), reader.readRecord());
        assertEquals(2, reader.getRecordNumber());
        assertEquals(3, reader.getLineNumber());

        assertNull(reader.readRecord());
        reader.close();
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
        reader.close();
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
    public void rfc7_escape_quote() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"b\"\"bb\",\"ccc\"");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "b\"bb", "ccc"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
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
        reader.close();
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
        reader.close();
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
        assertEquals(0, reader.getRecordNumber());
        assertArrayEquals(a("aaa", "bbb", "ccc"), reader.readRecord());
        assertArrayEquals(a("zzz", "yyy", "xxx"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
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
        assertEquals(0, reader.getRecordNumber());
        assertArrayEquals(a("aaa", "bb,b", "ccc"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
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
        assertEquals(0, reader.getRecordNumber());
        assertArrayEquals(a("aaa", "bb\tb", "ccc"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * ＜RFC拡張＞
     * 
     * 要素がクォートされている場合、区切り文字と要素との間にあるスペースは無視する。
     * 
     * aaa, "bbb" ,ccc
     */
    @Test
    public void space1() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aaa , \"bbb\" , ccc , ,");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa ", "bbb", " ccc ", " ", ""),
                reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    @Test
    public void space2() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"bbb\" ");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("bbb"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    @Test
    public void space3() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"a\" \r" + " \"b\" \n"
                + " \"c\" \r\n" + " \"d\" ");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("a"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertArrayEquals(a("b"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertArrayEquals(a("c"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertArrayEquals(a("d"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * クォートされた要素と見せかけて実は前後にスペースを持つ通常の要素である場合、をフォローする。
     * 
     * これはフォーマットエラーとはみなさない。
     */
    @Test
    public void space4() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("a, \"b\"bb\" ,c");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("a", " \"b\"bb\" ", "c"), reader.readRecord());
        // これはエラーではないためOKとみなしたい。
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /**
     * データ中にnull文字があっても、扱えること。(null文字で終了しないこと)
     */
    @Test
    public void null_char() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("aa" + '\u0000' + "a,bbb");

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNumber());
        assertArrayEquals(a("aa" + '\u0000' + "a", "bbb"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /**
     * データ中の空要素は""として読むこと。
     */
    @Test
    public void empty_element() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open(",aa,, ,   ,bbb,");

        // ## Act ##
        // ## Assert ##
        assertEquals(0, reader.getRecordNumber());
        assertArrayEquals(a("", "aa", "", " ", "   ", "bbb", ""),
                reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /**
     * クォート文字をシングルクォートへ変更できること。
     */
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
        reader.close();
    }

    /**
     * クォートだけの入力は、クォート1文字だけの1要素とみなす。
     */
    @Test
    public void only_quote_record() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("\""), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /**
     * 空行をskipして読めること。
     * その際に、データ部の改行による空行を除かないこと。
     */
    @Test
    public void skip_emptyline() throws Throwable {
        // ## Arrange ##

        final LineReaderHandler lineReaderHandler = new LineReaderHandler() {

            @Override
            public Line readLine(final LineReader lineReader, final Line line)
                    throws IOException {
                return lineReader.readLine(line);
            }

            @Override
            public boolean acceptLine(final Line line,
                    final ElementParserContext parserContext) {
                if (parserContext.isInElement()) {
                    return true;
                }
                if (line.getBody().length() == 0) {
                    return false;
                }
                return true;
            }

        };
        final Rfc4180Reader reader = open("\"a1\"\r\n" + "\r\n"
                + "\"b\r\n\r\n2\"", lineReaderHandler);

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("a1"), reader.readRecord());
        assertArrayEquals(a("b\r\n\r\n2"), reader.readRecord());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * 不正データ
     * 
     * クォートされた要素内で、EOFになった場合
     * 
     * "aaa","bbb","cc
     * 
     * 途切れた要素を、1要素として読み込む
     */
    @Test
    public void invalid_eof_inQuotedElement() throws Throwable {
        // ## Arrange ##
        final Rfc4180Reader reader = open("\"aaa\",\"bbb\",\"cc");

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("aaa", "bbb", "cc"), reader.readRecord());
        // invalid扱いとしたい
        assertEquals(Rfc4180Reader.RecordState.INVALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * 不正データ
     * 
     * 余分なクォートがある場合に、どう振る舞うのが良いか。。
     * → "b""部分をそのままの要素とする
     * 
     * a,"b"","c" CRLF
     * A,"B",C
     * 
     * 1行目が不正
     */
    @Test
    public void invalid_quote1() throws Throwable {
        // ## Arrange ##
        final String in = "a,\"b\"\",\"c\"" + "\r\n" + "A,\"B\",C";
        final Rfc4180Reader reader = open(in);

        // ## Act ##
        // ## Assert ##
        /*
         * readした後に、stateがINVALIDかどうかを見れるようにした。
         */
        assertArrayEquals(a("a", "\"b\"\"", "c"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.INVALID, reader.getRecordState());
        assertArrayEquals(a("A", "B", "C"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * 不正データ
     * 
     * 余分なクォートがある場合に、どう振る舞うのが良いか。。
     * → "b""部分をそのままの要素とする
     * 
     * a,"b"",c CRLF
     * A,"B",C CRLF
     * D, E, F
     * 
     * 1行目だけが不正
     */
    @Test
    public void invalid_quote2() throws Throwable {
        // ## Arrange ##
        final String in = "a,\"b\"\",c" + "\r\n" + "A,\"B\",C" + "\r\n"
                + "D,E,F";
        final Rfc4180Reader reader = open(in);

        // ## Act ##
        // ## Assert ##
        /*
         * ここでは、クォートが再度登場するまで食べに行く実装にした。
         * 
         * クォートが正しく登場すれば「何かおかしい」ことに気づける。
         * クォートが登場しない場合は、最後まで食べに行ってしまう。
         * 「前行に比べてデータ量がやけに多い」という判別くらいしかできなそう。。
         */
        assertArrayEquals(a("a", "\"b\"\"", "c"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.INVALID, reader.getRecordState());
        assertArrayEquals(a("A", "B", "C"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertArrayEquals(a("D", "E", "F"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * 不正データ
     * 
     * 余分なクォートがある場合に、どう振る舞うのが良いか。。
     * → "B" ""部分をそのままの要素とする
     * 
     * a,"b""",c CRLF
     * A,"B" "",C
     * 
     * 2行目が不正
     */
    @Test
    public void invalid_quote3() throws Throwable {
        // ## Arrange ##
        final String in = "a,\"b\"\"\",c" + "\r\n" + "A,\"B\" \"\",C";
        final Rfc4180Reader reader = open(in);

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("a", "b\"", "c"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertArrayEquals(a("A", "\"B\" \"\"", "C"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.INVALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * 不正データ
     * 
     * 余分なクォートがある場合に、どう振る舞うのが良いか。。
     * → "b" ""部分をそのままの要素とする
     * 
     * a,"b" "" CRLF
     * A,B,C
     * 
     * 1行目が不正
     */
    @Test
    public void invalid_quote4() throws Throwable {
        // ## Arrange ##
        final String in = "a,\"b\" \"\"" + "\r\n" + "A,B,C";
        final Rfc4180Reader reader = open(in);

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("a", "\"b\" \"\""), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.INVALID, reader.getRecordState());
        assertArrayEquals(a("A", "B", "C"), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.VALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    /*
     * 不正データ
     * 
     * 先頭に不正データが登場するパターン
     */
    @Test
    public void invalid_quote5() throws Throwable {
        // ## Arrange ##
        final String in = "\"b\" \"\"";
        System.out.println(in);
        final Rfc4180Reader reader = open(in);

        // ## Act ##
        // ## Assert ##
        assertArrayEquals(a("\"b\" \"\""), reader.readRecord());
        assertEquals(Rfc4180Reader.RecordState.INVALID, reader.getRecordState());
        assertNull(reader.readRecord());
        reader.close();
    }

    protected Rfc4180Reader open(final String text) {
        return open(text, (Character) null, (LineReaderHandler) null);
    }

    protected Rfc4180Reader open(final String text,
            final LineReaderHandler lineReaderHandler) {
        return open(text, (Character) null, lineReaderHandler);
    }

    protected Rfc4180Reader open(final String text,
            final Character elementSeparator) {
        return open(text, elementSeparator, (LineReaderHandler) null);
    }

    protected Rfc4180Reader open(final String text,
            final Character elementSeparator,
            final LineReaderHandler lineReaderHandler) {
        final StringReader reader = new StringReader(text);
        final Rfc4180Reader csvReader = new Rfc4180Reader();
        if (elementSeparator != null) {
            csvReader.setElementSeparator(elementSeparator);
        }
        if (lineReaderHandler != null) {
            csvReader.setLineReaderHandler(lineReaderHandler);
        }
        csvReader.open(reader);
        return csvReader;
    }

    private void assertArrayEquals(final String[] expected,
            final String[] actual) {
        csvAssert_.assertArrayEquals(expected, actual);
    }

}
