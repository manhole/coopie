package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.junit.Test;

public class LineReadableTest {

    @Test
    public void readLineBody1() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123456789");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals("0123456789", line);
        assertEquals(1, r.getLineNumber());
        assertEquals(LineSeparator.NONE, r.getLineSeparator());

        assertEquals(null, r.readLineBody());
        assertEquals(1, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLineBody2() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123456789\r\n");
        assertEquals(0, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals("0123456789", line);
        assertEquals(1, r.getLineNumber());
        assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        assertEquals(null, r.readLineBody());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLineBody3() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        {
            final String line = r.readLineBody();
            assertEquals(1, r.getLineNumber());
            assertEquals("0123", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(2, r.getLineNumber());
            assertEquals("45678", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(3, r.getLineNumber());
            assertEquals("", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(4, r.getLineNumber());
            assertEquals("9", line);
            assertEquals(LineSeparator.CRLF, r.getLineSeparator());
        }

        assertEquals(null, r.readLineBody());
        assertEquals(4, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLine3_1() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        final Line line = new LineImpl();
        {
            assertEquals(true, r.readLine(line));
            assertEquals(1, line.getNumber());
            assertEquals("0123", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            assertEquals(true, r.readLine(line));
            assertEquals(2, line.getNumber());
            assertEquals("45678", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            assertEquals(true, r.readLine(line));
            assertEquals(3, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            assertEquals(true, r.readLine(line));
            assertEquals(4, line.getNumber());
            assertEquals("9", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }

        assertEquals(false, r.readLine(line));
        assertEquals(4, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLine3_2() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(1, line.getNumber());
            assertEquals("0123", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(2, line.getNumber());
            assertEquals("45678", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(3, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertNotNull(line);
            assertEquals(4, line.getNumber());
            assertEquals("9", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }

        final Line line = r.readLine();
        assertNull(line);
        assertEquals(4, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    @Test
    public void readLineBody4() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("\n0123\n\n456\r789");

        // ## Act ##
        // ## Assert ##

        {
            final String line = r.readLineBody();
            assertEquals(1, r.getLineNumber());
            assertEquals("", line);
            assertEquals(LineSeparator.LF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(2, r.getLineNumber());
            assertEquals("0123", line);
            assertEquals(LineSeparator.LF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(3, r.getLineNumber());
            assertEquals("", line);
            assertEquals(LineSeparator.LF, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(4, r.getLineNumber());
            assertEquals("456", line);
            assertEquals(LineSeparator.CR, r.getLineSeparator());
        }
        {
            final String line = r.readLineBody();
            assertEquals(5, r.getLineNumber());
            assertEquals("789", line);
            assertEquals(LineSeparator.NONE, r.getLineSeparator());
        }

        assertEquals(null, r.readLineBody());
        assertEquals(5, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    /*
     * 空文字
     */
    @Test
    public void readLineBody5() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals(null, line);
        assertEquals(0, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        assertEquals(null, r.readLineBody());
        assertEquals(0, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    /*
     * スペースのみ
     */
    @Test
    public void readLineBody6() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create(" ");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final String line = r.readLineBody();

        // ## Assert ##
        assertEquals(" ", line);
        assertEquals(1, r.getLineNumber());
        assertEquals(LineSeparator.NONE, r.getLineSeparator());

        assertEquals(null, r.readLineBody());
        assertEquals(1, r.getLineNumber());
        assertEquals(null, r.getLineSeparator());

        r.close();
    }

    private LineReadable create(final String in) {
        final Readable readable = new StringReader(in);
        return new LineReadable(readable);
    }

}
