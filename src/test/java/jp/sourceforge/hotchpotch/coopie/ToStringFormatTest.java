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
        // ## Assert ##
        assertEquals("abc", new ToStringFormat().format("abc"));
    }

    @Test
    public void formatInteger() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##
        assertEquals("4321", new ToStringFormat().format(4321));
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
        // ## Assert ##
        assertEquals("2123/03/09 14:01:02.065",
                new ToStringFormat().format(date));
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
        // ## Assert ##
        assertEquals("2123-03-09", new ToStringFormat().format(date));
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
        // ## Assert ##
        assertEquals("14:01:02", new ToStringFormat().format(date));
    }

    @Test
    public void formatObject() throws Throwable {
        // ## Arrange ##
        final Foo foo = new Foo();
        foo.setAaa("a1");

        // ## Act ##
        // ## Assert ##
        assertEquals("Foo[aaa=a1, bbbBbb=<null>]",
                new ToStringFormat().format(foo));
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
        // ## Assert ##
        assertEquals("Bar[aaa=a1]", new ToStringFormat().format(foo));
    }

    @Test
    public void formatNull() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        // ## Assert ##

        assertEquals("<null>", new ToStringFormat().format(null));
    }

    @Test
    public void composit1() throws Throwable {
        // ## Arrange ##
        final Composit c1 = new Composit("c1");
        c1.setComposit(c1);

        // ## Act ##

        // ## Assert ##
        assertEquals("Composit[composit=<..>, name=c1]",
                new ToStringFormat().format(c1));
    }

    @Test
    public void composit2() throws Throwable {
        // ## Arrange ##
        final Composit c1 = new Composit("c1");
        final Composit c2 = new Composit("c2");
        c1.setComposit(c2);
        c2.setComposit(c1);

        // ## Act ##

        // ## Assert ##
        assertEquals(
                "Composit[composit=Composit[composit=<..>, name=c2], name=c1]",
                new ToStringFormat().format(c1));
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

}
