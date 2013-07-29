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

import java.lang.annotation.Annotation

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumn
import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting

import org.junit.Test
import org.t2framework.commons.meta.BeanDesc
import org.t2framework.commons.meta.BeanDescFactory
import org.t2framework.commons.meta.PropertyDesc

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

    static class Aaa {
        @CsvColumn(label="AAA")
        String aa
        @CsvColumn(label="BBB")
        String bb
        @CsvColumn(label="CCC")
        String ccc
    }

}
