package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ToStringFormatTest {

    @Test
    public void format() throws Throwable {
        // ## Arrange ##
        final Foo foo = new Foo();
        foo.setAaa("a1");

        // ## Act ##
        // ## Assert ##

        assertEquals("Foo[aaa=a1, bbbBbb=<null>]",
                new ToStringFormat().format(foo));
    }

    @Test
    public void composit1() throws Throwable {
        // ## Arrange ##
        final Composit c1 = new Composit("c1");
        c1.setComposit(c1);

        // ## Act ##

        // ## Assert ##
        assertEquals(
                "Composit[composit=Composit[composit=<..>, name=c1], name=c1]",
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
