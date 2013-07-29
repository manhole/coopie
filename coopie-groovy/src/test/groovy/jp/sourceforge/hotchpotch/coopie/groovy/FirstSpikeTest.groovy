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

import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.Rfc4180Reader
import groovy.util.GroovyTestCase

class FirstSpikeTest extends GroovyTestCase {
    void testSomething() {

        def reader = new StringReader("""
aaa,bbb,ccc
a1,b1,c1
a2,b2,c2
""".trim())

        final Rfc4180Reader csvReader = new Rfc4180Reader()
        csvReader.setElementSeparator(CsvSetting.COMMA)
        csvReader.setQuoteMark(CsvSetting.DOUBLE_QUOTE)
        csvReader.open(reader)
        assert "aaa|bbb|ccc".split("\\|") == csvReader.readRecord()
        assert "a1|b1|c1".split("\\|") == csvReader.readRecord()
        assert "a2|b2|c2".split("\\|") == csvReader.readRecord()
        assert null == csvReader.readRecord()
        csvReader.close()
    }
}
