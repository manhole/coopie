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
import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.QuoteMode
import jp.sourceforge.hotchpotch.coopie.groovy.util.TextAssert
import jp.sourceforge.hotchpotch.coopie.util.LineSeparator

import org.junit.Test

class CsvWriterTest {

    @Test
    public void write1() {
        def sw = new StringWriter()
        new Csv(elementSeparator: CsvSetting.COMMA, quoteMode: QuoteMode.MINIMUM, lineSeparator: LineSeparator.LF).withWriter(sw) { writer ->
            writer << ["AAA", "BBB", "CCC"]
            writer << ["a1", "b1", "c1"]
            writer << ["a2", "", " c2"]
        }

        def expected = """AAA,BBB,CCC
a1,b1,c1
a2,, c2
"""

        def ta = new TextAssert()
        ta.assertText(expected, sw.toString())
    }
}
