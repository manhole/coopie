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

package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

import org.junit.Test;

public class CsvExampleTest {

    @Test
    public void writeCsv1() throws Throwable {
        // ## Arrange ##
        final StringWriter dest = new StringWriter();

        // ## Act ##
        final RecordWriter<SomeBean> writer = SomeCsvIO.openWriter(SomeBean.class, dest);
        writer.write(new SomeBean("a1", "b1", "c1"));
        writer.write(new SomeBean("a2", "b2", "c2"));
        writer.close();

        // ## Assert ##
        final String actual = dest.toString();
        assertEquals("aaa,bbb,ccc\r\n" + "a1,b1,c1\r\n" + "a2,b2,c2\r\n", actual);
    }

    @Test
    public void readCsv1() throws Throwable {
        // ## Arrange ##
        final StringReader source = new StringReader("aaa,bbb,ccc\r\n" + "a1,b1,c1\r\n" + "a2,b2,c2\r\n");

        // ## Act ##
        final RecordReader<SomeBean> reader = SomeCsvIO.openReader(SomeBean.class, source);

        // ## Assert ##
        final SomeBean record = new SomeBean();
        assertEquals(true, reader.hasNext());
        reader.read(record);
        assertArrayEquals(a("a1", "b1", "c1"), a(record.getAaa(), record.getBbb(), record.getCcc()));
        assertEquals(true, reader.hasNext());
        reader.read(record);
        assertArrayEquals(a("a2", "b2", "c2"), a(record.getAaa(), record.getBbb(), record.getCcc()));
        assertEquals(false, reader.hasNext());
        reader.close();
    }

    /*
     * CSVレイアウト(改行文字や区切り文字など)は設計で決まるため、
     * レイアウト情報と利用クラスを分けておくことになるだろう。
     */
    static class SomeCsvIO {

        static <BEAN> RecordWriter<BEAN> openWriter(final Class<BEAN> clazz, final Appendable appendable) {
            final BeanCsvLayout<BEAN> layout = createLayout(clazz);
            final RecordWriter<BEAN> writer = layout.openWriter(appendable);
            return writer;
        }

        static <BEAN> RecordReader<BEAN> openReader(final Class<BEAN> clazz, final Readable readable) {
            final BeanCsvLayout<BEAN> layout = createLayout(clazz);
            final RecordReader<BEAN> reader = layout.openReader(readable);
            return reader;
        }

        private static <BEAN> BeanCsvLayout<BEAN> createLayout(final Class<BEAN> clazz) {
            final BeanCsvLayout<BEAN> layout = BeanCsvLayout.getInstance(clazz);
            // 要素区切り文字をカンマ","にする
            layout.setElementSeparator(CsvSetting.COMMA);
            // 要素を必要なときのみクォートする
            layout.setQuoteMode(QuoteMode.MINIMUM);
            return layout;
        }

    }

    /*
     * CSV 1行に対応するデータ
     */
    public static class SomeBean {

        private String aaa_;
        private String bbb_;
        private String ccc_;

        public SomeBean() {
        }

        public SomeBean(final String aaa, final String bbb, final String ccc) {
            aaa_ = aaa;
            bbb_ = bbb;
            ccc_ = ccc;
        }

        @CsvColumn(label = "aaa", order = 0)
        public String getAaa() {
            return aaa_;
        }

        public void setAaa(final String aaa) {
            aaa_ = aaa;
        }

        @CsvColumn(label = "bbb", order = 1)
        public String getBbb() {
            return bbb_;
        }

        public void setBbb(final String bbb) {
            bbb_ = bbb;
        }

        @CsvColumn(label = "ccc", order = 2)
        public String getCcc() {
            return ccc_;
        }

        public void setCcc(final String ccc) {
            ccc_ = ccc;
        }

        private final ToStringFormat toStringFormat = new ToStringFormat();

        @Override
        public String toString() {
            return toStringFormat.format(this);
        }

    }

}
