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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;
import org.slf4j.Logger;

public class ToStringFormatTest {

    private static final Logger logger = LoggerFactory.getLogger();

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
        assertEquals("Class[jp.sourceforge.hotchpotch.coopie.util.ToStringFormatTest$Foo]", actual);
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
        format.setMaxDepth(2);
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
        format.setMaxDepth(2);
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
        format.setMaxDepth(2);
        final String actual = format.format(o);

        // ## Assert ##
        assertEquals("[Bar[aaa=b1], Bar[aaa=b2]]", actual);
    }

    @Test
    public void formatObjectArray3() throws Throwable {
        // ## Arrange ##
        final Integer[] o = new Integer[] { new Integer("5"), new Integer("-12") };

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
    public void formatEmptyString() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format("");

        // ## Assert ##
        assertEquals("<empty>", actual);
    }

    @Test
    public void formatUnprintableString1() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format("aaa\r\nbbb");

        // ## Assert ##
        assertEquals("aaa<CR><LF>bbb", actual);
    }

    @Test
    public void formatUnprintableString2() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format("\tat");

        // ## Assert ##
        assertEquals("<TAB>at", actual);
    }

    @Test
    public void composite1() throws Throwable {
        // ## Arrange ##
        final Composite c1 = new Composite("c1");
        c1.setComposite(c1);

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(c1);

        // ## Assert ##
        assertEquals("Composite[composite=<..>, name=c1]", actual);
    }

    @Test
    public void composite2() throws Throwable {
        // ## Arrange ##
        final Composite c1 = new Composite("c1");
        final Composite c2 = new Composite("c2");
        c1.setComposite(c2);
        c2.setComposite(c1);

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        final String actual = format.format(c1);

        // ## Assert ##
        // 実際はこのような値
        // Composite[composite=Composite@f2a55aa, name=c1]
        logger.debug(actual);
        assertThat(actual, containsString("Composite[composite=Composite@"));
        assertThat(actual, containsString(", name=c1]"));
        assertThat(actual, startsWith("Composite[composite=Composite@"));
        assertThat(actual, endsWith(", name=c1]"));
        //assertEquals("Composite[composite=Composite@xxxxxx, name=c1]", actual);
    }

    @Test
    public void composite3() throws Throwable {
        // ## Arrange ##
        final Composite c1 = new Composite("c1");
        final Composite c2 = new Composite("c2");
        c1.setComposite(c2);
        c2.setComposite(c1);

        // ## Act ##
        final ToStringFormat format = new ToStringFormat();
        format.setMaxDepth(2);
        final String actual = format.format(c1);

        // ## Assert ##
        assertEquals("Composite[composite=Composite[composite=<..>, name=c2], name=c1]", actual);
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

    public static class Composite {

        private String name;
        private Composite composite;

        public Composite(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Composite getComposit() {
            return composite;
        }

        public void setComposite(final Composite composit) {
            composite = composit;
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
