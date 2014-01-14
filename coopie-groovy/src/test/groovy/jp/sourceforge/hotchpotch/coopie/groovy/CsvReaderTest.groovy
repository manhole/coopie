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

package jp.sourceforge.hotchpotch.coopie.groovy

import static org.junit.Assert.*

import java.text.SimpleDateFormat
import java.util.Date

import jp.sourceforge.hotchpotch.coopie.csv.Converter
import jp.sourceforge.hotchpotch.coopie.csv.CsvColumn
import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.DefaultConverterRepository

import org.junit.Test

class CsvReaderTest {

    @Test
    public void read1() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1,c1
a2,, c2
""".trim())
        int index = -1
        new Csv(elementSeparator: CsvSetting.COMMA).openReader(input).eachRecord { record ->
            index++
            if (index == 0) {
                assert ["AAA", "BBB", "CCC"]== record
            } else if (index == 1) {
                assert ["a1", "b1", "c1"]== record
            } else if (index == 2) {
                assert ["a2", "", " c2"]== record
            }
        }

        assert 2 == index
    }

    /*
     * こっちの短い書き方の方が良いかな。
     * Readerを表に見せなくて済むし。
     */
    @Test
    public void read2() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1,c1
a2,, c2
""".trim())
        int index = -1
        new Csv(elementSeparator: CsvSetting.COMMA).eachRecord(input) { record ->
            index++
            if (index == 0) {
                assert ["AAA", "BBB", "CCC"]== record
            } else if (index == 1) {
                assert ["a1", "b1", "c1"]== record
            } else if (index == 2) {
                assert ["a2", "", " c2"]== record
            }
        }

        assert 2 == index
    }

    /*
     * CSV要素を展開して受け取れる
     */
    @Test
    public void read_record_expand1() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1,c1
a2,, c2
""".trim())
        int index = -1
        new Csv().eachRecord(input) { r1, r2, r3 ->
            index++
            if (index == 0) {
                assert ["AAA", "BBB", "CCC"]== [r1, r2, r3]
            } else if (index == 1) {
                assert ["a1", "b1", "c1"]== [r1, r2, r3]
            } else if (index == 2) {
                assert ["a2", "", " c2"]== [r1, r2, r3]
            }
        }

        assert 2 == index
    }

    /*
     * CSV要素を展開して受け取る引数がCSV項目数より多い場合は、nullが渡される。
     */
    @Test
    public void read_record_expand2() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1, 
a2,, c2
""".trim())
        int index = -1
        new Csv().eachRecord(input) { r1, r2, r3, r4 ->
            index++
            if (index == 0) {
                assert ["AAA", "BBB", "CCC", null]== [r1, r2, r3, r4]
            } else if (index == 1) {
                assert ["a1", "b1", " ", null]== [r1, r2, r3, r4]
            } else if (index == 2) {
                assert ["a2", "", " c2", null]== [r1, r2, r3, r4]
            }
        }

        assert 2 == index
    }

    /*
     * CSV要素を展開して受け取る引数がCSV項目数より少ない場合は、後ろの項目は渡されない。
     */
    @Test
    public void read_record_expand3() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1, 
a2,, c2
""".trim())
        int index = -1
        new Csv().eachRecord(input) { r1, r2 ->
            index++
            if (index == 0) {
                assert ["AAA", "BBB"]== [r1, r2]
            } else if (index == 1) {
                assert ["a1", "b1"]== [r1, r2]
            } else if (index == 2) {
                assert ["a2", ""]== [r1, r2]
            }
        }

        assert 2 == index
    }

    // 空白をtrimする
    @Test
    public void readTrim1() {
        def input = new StringReader("""
AAA,BBB,CCC
a1, b1 , c1
a2, , c2
""".trim())
        int index = -1
        new Csv(elementSeparator: CsvSetting.COMMA, elementEditor: { it.trim() }).eachRecord(input) { record ->
            index++
            if (index == 0) {
                assert ["AAA", "BBB", "CCC"]== record
            } else if (index == 1) {
                assert ["a1", "b1", "c1"]== record
            } else if (index == 2) {
                assert ["a2", "", "c2"]== record
            }
        }

        assert 2 == index
    }

    /*
     */
    @Test
    public void readAsMap1() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1,c1
a2,, c2
""".trim())
        int index = -1
        new Csv().eachRecordAsMap(input) { record ->
            index++
            //println record
            if (index == 0) {
                assert ["AAA":"a1","BBB":"b1","CCC":"c1"] == record
            } else if (index == 1) {
                assert ["AAA":"a2","BBB":null,"CCC":" c2"] == record
            }
        }

        assert 1 == index
    }

    @Test
    public void readAsBean1() {
        def input = new StringReader("""
AAA,BBB,CCC
a1,b1,c1
a2,, c2
""".trim())
        int index = -1
        new Csv().eachRecordAsBean(input, Aaa) { record ->
            index++
            //println record
            if (index == 0) {
                assert record.aa == "a1"
                assert record.bb == "b1"
                assert record.ccc == "c1"
            } else if (index == 1) {
                assert record.aa == "a2"
                assert record.bb == null
                assert record.ccc == " c2"
            }
        }

        assert 1 == index
    }

    @Test
    public void readAsBean_date() {
        def input = new StringReader("""
aa,bb
a1,20131203T144302
""".trim())

        def repo = new DefaultConverterRepository()
        repo.register(new DateConverter())
        int index = -1
        new Csv(converterRepository: repo).eachRecordAsBean(input, Bbb) { record ->
            index++
            //println record
            if (index == 0) {
                assert record.aa == "a1"
                assert record.bb == new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").parse("2013/12/03 14:43:02.000")
            }
        }

        assert 0 == index
    }
    static class Aaa {
        @CsvColumn(label="AAA", order=0)
        String aa
        @CsvColumn(label="BBB", order=1)
        String bb
        @CsvColumn(label="CCC", order=2)
        String ccc
    }

    static class Bbb {
        @CsvColumn(label="aa", order=0)
        String aa
        @CsvColumn(label="bb", order=1)
        Date bb
    }

    static class DateConverter implements Converter<Date, String> {

        def format = new SimpleDateFormat("yyyyMMdd'T'HHmmss")

        @Override
        public String convertTo(Date from) {
            return format.format(from)
        }

        @Override
        public Date convertFrom(String from) {
            return format.parse(from)
        }

    }

}
