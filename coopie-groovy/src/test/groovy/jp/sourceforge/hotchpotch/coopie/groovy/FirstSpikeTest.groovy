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
