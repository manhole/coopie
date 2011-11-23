package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

public class LineReadableTest {

    @Test
    public void readLine1() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123456789");
        assertEquals(0, r.getLineNumber());

        // ## Act ##
        final Line line = r.readLine();

        // ## Assert ##
        assertEquals(0, line.getNumber());
        assertEquals("0123456789", line.getBody());
        assertEquals(LineSeparator.NONE, line.getSeparator());
        assertEquals(1, r.getLineNumber());

        assertEquals(null, r.readLine());
        assertEquals(1, r.getLineNumber());
    }

    @Test
    public void readLine2() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123456789\r\n");

        // ## Act ##
        final Line line = r.readLine();

        // ## Assert ##
        assertEquals(0, line.getNumber());
        assertEquals("0123456789", line.getBody());
        assertEquals(LineSeparator.CRLF, line.getSeparator());
        assertEquals(null, r.readLine());
    }

    @Test
    public void readLine3() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("0123\r\n45678\r\n\r\n9\r\n");

        // ## Act ##
        // ## Assert ##
        {
            final Line line = r.readLine();
            assertEquals(0, line.getNumber());
            assertEquals("0123", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
            assertEquals(1, r.getLineNumber());
        }
        {
            final Line line = r.readLine();
            assertEquals(1, line.getNumber());
            assertEquals("45678", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertEquals(2, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
        }
        {
            final Line line = r.readLine();
            assertEquals(3, line.getNumber());
            assertEquals("9", line.getBody());
            assertEquals(LineSeparator.CRLF, line.getSeparator());
            assertEquals(4, r.getLineNumber());
        }

        assertEquals(null, r.readLine());
        assertEquals(4, r.getLineNumber());
    }

    @Test
    public void readLine4() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("\n0123\n\n456\r789");

        // ## Act ##
        // ## Assert ##

        {
            final Line line = r.readLine();
            assertEquals(0, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.LF, line.getSeparator());
            assertEquals(1, r.getLineNumber());
        }
        {
            final Line line = r.readLine();
            assertEquals(1, line.getNumber());
            assertEquals("0123", line.getBody());
            assertEquals(LineSeparator.LF, line.getSeparator());
            assertEquals(2, r.getLineNumber());
        }
        {
            final Line line = r.readLine();
            assertEquals(2, line.getNumber());
            assertEquals("", line.getBody());
            assertEquals(LineSeparator.LF, line.getSeparator());
            assertEquals(3, r.getLineNumber());
        }
        {
            final Line line = r.readLine();
            assertEquals(3, line.getNumber());
            assertEquals("456", line.getBody());
            assertEquals(LineSeparator.CR, line.getSeparator());
            assertEquals(4, r.getLineNumber());
        }
        {
            final Line line = r.readLine();
            assertEquals(4, line.getNumber());
            assertEquals("789", line.getBody());
            assertEquals(LineSeparator.NONE, line.getSeparator());
            assertEquals(5, r.getLineNumber());
        }

        assertEquals(null, r.readLine());
        assertEquals(5, r.getLineNumber());
    }

    private LineReadable create(final String in) {
        final Readable readable = new StringReader(in);
        return new LineReadable(readable);
    }

}
