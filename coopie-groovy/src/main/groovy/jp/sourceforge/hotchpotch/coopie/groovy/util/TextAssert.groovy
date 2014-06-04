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
