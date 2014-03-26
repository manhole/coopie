package jp.sourceforge.hotchpotch.coopie.groovy.util

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil
import jp.sourceforge.hotchpotch.coopie.util.LineReader

class TextAssert {

    def strictLineSeparator = true

    /*
     * 期待値が左側
     * http://groovy.codehaus.org/Unit+Testing
     */
    def assertText(String expected, String actual) {
        def expectedReader = new LineReader(new StringReader(expected))
        def actualReader = new LineReader(new StringReader(actual))
        assertText(expectedReader, actualReader)
    }

    def assertText(LineReader expected, LineReader actual) {
        try {
            while (true) {
                def expectedLine = expected.readLine()
                def actualLine = actual.readLine()
                if (expectedLine == null && actualLine == null) {
                    break
                }
                if (strictLineSeparator) {
                    assert expectedLine?.bodyAndSeparator == actualLine?.bodyAndSeparator
                } else {
                    assert expectedLine?.body == actualLine?.body
                }
            }
        } finally {
            CloseableUtil.closeNoException(expected)
            CloseableUtil.closeNoException(actual)
        }
    }
}
