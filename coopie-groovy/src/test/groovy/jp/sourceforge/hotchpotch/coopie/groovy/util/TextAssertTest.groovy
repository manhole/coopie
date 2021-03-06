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

import org.junit.Test

class TextAssertTest {

    @Test
    void sametext() {
        def ta = new TextAssert()
        ta.assertText("abc", "abc")
        ta.assertText("abc\r\n", "abc\r\n")
        ta.assertText("abc\r\nd", "abc\r\nd")
        ta.assertText("abc\r\ndef", "abc\r\ndef")
    }

    @Test
    void different() {
        def ta = new TextAssert()
        _assertFail {
            ta.assertText("", "\n")
        }
        _assertFail {
            ta.assertText("abc", "abc\n")
        }
        _assertFail {
            ta.assertText("abc\n", "abc\nde")
        }
        _assertFail {
            ta.assertText("abc\nde", "abc\n")
        }
    }

    @Test
    void ignoreLineSeparator() {
        def ta = new TextAssert(strictLineSeparator: false)
        ta.assertText("abc", "abc\n")
        ta.assertText("abc\r\nde", "abc\nde")
        //ta.assertText("", "\r\n")
        _assertFail {
            ta.assertText("abc\r\nde", "abc\ndef")
        }
        _assertFail {
            ta.assertText("", "\r\n\n")
        }
    }


    def _assertFail(Closure c) {
        boolean success = false
        try {
            c.call()
        } catch (AssertionError e) {
            println e.getMessage()
            success = true
        }
        assert success
    }
}
