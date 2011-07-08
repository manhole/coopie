package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class ToStringFormatTest {

    @Test
    public void formatString() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format("abc");

        // ## Assert ##
        assertEquals("abc", actual);
    }

    @Test
    public void formatInteger() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(4321);

        // ## Assert ##
        assertEquals("4321", actual);
    }

    @Test
    public void formatLong() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(4321L);

        // ## Assert ##
        assertEquals("4321", actual);
    }

    @Test
    public void formatFloat1() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(4321.98F);

        // ## Assert ##
        assertEquals("4321.98", actual);
    }

    @Test
    public void formatFloat2() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(4321.9876F);

        // ## Assert ##
        // 丸まってしまうようだ
        assertEquals("4321.988", actual);
    }

    @Test
    public void formatBoolean() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(false);

        // ## Assert ##
        assertEquals("false", actual);
    }

    @Test
    public void formatUtilDate() throws Throwable {
        // ## Arrange ##
        final Calendar c = Calendar.getInstance();
        c.clear();
        c.set(2123, 2, 9, 14, 1, 2);
        c.set(Calendar.MILLISECOND, 65);
        final Date date = c.getTime();

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(date);

        // ## Assert ##
        assertEquals("2123/03/09 14:01:02.065", actual);
    }

    /*
     * SQL Dateは年月日のみ。
     */
    @Test
    public void formatSqlDate() throws Throwable {
        // ## Arrange ##
        final Calendar c = Calendar.getInstance();
        c.clear();
        c.set(2123, 2, 9, 14, 1, 2);
        c.set(Calendar.MILLISECOND, 65);
        final Date date = new java.sql.Date(c.getTimeInMillis());

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(date);

        // ## Assert ##
        assertEquals("2123-03-09", actual);
    }

    /*
     * SQL Timeは時分秒のみ。
     * ミリ秒は持たない。
     */
    @Test
    public void formatSqlTime() throws Throwable {
        // ## Arrange ##
        final Calendar c = Calendar.getInstance();
        c.clear();
        c.set(2123, 2, 9, 14, 1, 2);
        c.set(Calendar.MILLISECOND, 65);
        final Date date = new java.sql.Time(c.getTimeInMillis());

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(date);

        // ## Assert ##
        assertEquals("14:01:02", actual);
    }

    @Test
    public void formatClass() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(Foo.class);

        // ## Assert ##
        assertEquals(
                "Class[jp.sourceforge.hotchpotch.coopie.ToStringFormatTest$Foo]",
                actual);
    }

    @Test
    public void formatObject1() throws Throwable {
        // ## Arrange ##
        final Foo foo = new Foo();
        foo.setAaa("a1");

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(foo);

        // ## Assert ##
        assertEquals("Foo[aaa=a1, bbbBbb=<null>]", actual);
    }

    /*
     * setterだけのプロパティは対象外
     */
    @Test
    public void formatObject2() throws Throwable {
        // ## Arrange ##
        final Bar foo = new Bar();
        foo.setAaa("a1");

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(foo);

        // ## Assert ##
        assertEquals("Bar[aaa=a1]", actual);
    }

    /*
     * クラスのアクセス修飾子が狭い(private static class)ために
     * アクセスできないgetterは、強引に実行する。
     * 
     * メソッドのアクセス修飾子がpublicでない場合は、対象外とする。
     */
    @Test
    public void formatObject3() throws Throwable {
        // ## Arrange ##
        final Buzz2 foo = new Buzz2();
        foo.setAaa("a1");

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(foo);

        // ## Assert ##
        assertEquals("Buzz2[aaa=a1, bbb=<null>]", actual);
    }

    /*
     * アクセス修飾子が狭いために
     * アクセスできないクラスは対象外。
     * toStringの結果とする。
     */
    @Test
    public void formatObject4() throws Throwable {
        // ## Arrange ##
        final Buzz1 foo1 = new Buzz1();
        final Buzz2 foo2 = new Buzz2();
        foo2.setAaa("a1");
        foo1.setAaa(foo2);

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(foo1);

        // ## Assert ##
        assertEquals("Buzz1[aaa=Buzz2[aaa=a1, bbb=<null>], bbb=<null>]", actual);
    }

    @Test
    public void formatObjectArray1() throws Throwable {
        // ## Arrange ##
        final Object[] o = new Object[] { new Bar("bar1") };

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(o);

        // ## Assert ##
        assertEquals("[Bar[aaa=bar1]]", actual);
    }

    @Test
    public void formatObjectArray2() throws Throwable {
        // ## Arrange ##
        final Object[] o = new Object[] { new Bar("b1"), new Bar("b2") };

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(o);

        // ## Assert ##
        assertEquals("[Bar[aaa=b1], Bar[aaa=b2]]", actual);
    }

    @Test
    public void formatObjectArray3() throws Throwable {
        // ## Arrange ##
        final Integer[] o = new Integer[] { new Integer("5"),
                new Integer("-12") };

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(o);

        // ## Assert ##
        assertEquals("[5, -12]", actual);
    }

    @Test
    public void formatNull() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(null);

        // ## Assert ##
        assertEquals("<null>", actual);
    }

    @Test
    public void composit1() throws Throwable {
        // ## Arrange ##
        final Composit c1 = new Composit("c1");
        c1.setComposit(c1);

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(c1);

        // ## Assert ##
        assertEquals("Composit[composit=<..>, name=c1]", actual);
    }

    @Test
    public void composit2() throws Throwable {
        // ## Arrange ##
        final Composit c1 = new Composit("c1");
        final Composit c2 = new Composit("c2");
        c1.setComposit(c2);
        c2.setComposit(c1);

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(c1);

        // ## Assert ##
        assertEquals(
                "Composit[composit=Composit[composit=<..>, name=c2], name=c1]",
                actual);
    }

    /*
     * getterで新たなinstanceを返し続けるオブジェクトへは相性が悪い。
     * StackOverflowErrorになってしまう。
     * 
     * そのため、
     * 今まではgetterでpropertyを取得していたが、直接fieldを見に行くようにする。
     */
    @Test
    public void loopGetter() throws Throwable {
        // ## Arrange ##

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(new LoopGetter());

        // ## Assert ##
        assertEquals("LoopGetter[instanceNo=1, totalNo=1]", actual);
    }

    public static class Foo {

        private String aaa;
        private String bbbBbb;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        public String getBbbBbb() {
            return bbbBbb;
        }

        public void setBbbBbb(final String bbbBbb) {
            this.bbbBbb = bbbBbb;
        }

    }

    public static class Bar {

        private String aaa;

        public Bar() {
        }

        public Bar(final String aaa) {
            setAaa(aaa);
        }

        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        public void setAaaAsInteger(final Integer aaa) {
            this.aaa = String.valueOf(aaa);
        }

    }

    public static class Buzz1 {

        private Buzz2 aaa;
        private String bbb;

        public Buzz2 getAaa() {
            return aaa;
        }

        public void setAaa(final Buzz2 aaa) {
            this.aaa = aaa;
        }

        protected String getBbb() {
            return bbb;
        }

        protected void setBbb(final String bbb) {
            this.bbb = bbb;
        }

    }

    private static class Buzz2 {

        private String aaa;
        private String bbb;

        public String getAaa() {
            return aaa;
        }

        public void setAaa(final String aaa) {
            this.aaa = aaa;
        }

        protected String getBbb() {
            return bbb;
        }

        protected void setBbb(final String bbb) {
            this.bbb = bbb;
        }

    }

    public static class Composit {

        private String name;
        private Composit composit;

        public Composit(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Composit getComposit() {
            return composit;
        }

        public void setComposit(final Composit composit) {
            this.composit = composit;
        }

        @Override
        public String toString() {
            return new ToStringFormat().format(this);
        }

    }

    public static class LoopGetter {

        private final int instanceNo;
        private static volatile int totalNo;

        public LoopGetter() {
            instanceNo = ++totalNo;
        }

        public LoopGetter getLoopGetter() {
            return new LoopGetter();
        }

        public int getNo() {
            return instanceNo;
        }

    }

}
