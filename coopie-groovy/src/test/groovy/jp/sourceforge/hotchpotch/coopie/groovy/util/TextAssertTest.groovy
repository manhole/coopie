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
