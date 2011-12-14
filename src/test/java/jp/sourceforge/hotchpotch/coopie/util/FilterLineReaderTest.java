package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;

import org.junit.Test;

public class FilterLineReaderTest {

    /**
     * 不要行を除く。
     * 
     * ここでは空行を除いている。
     */
    @Test
    public void filter() throws Throwable {
        // ## Arrange ##
        final LineReadable r = create("\r\n" + "a1\r\n" + "a2\n" + "\r\n"
                + "\r\n" + "a3" + "\n" + "\r");

        // ## Act ##
        final LineFilter filter = new SkipLineFilter();
        final LineReader rr = new FilterLineReader(r, filter);

        // ## Assert ##
        assertEquals("a1", rr.readLine().getBody());
        assertEquals(2, rr.getLineNumber());
        assertEquals("a2", rr.readLine().getBody());
        assertEquals(3, rr.getLineNumber());
        assertEquals("a3", rr.readLine().getBody());
        assertEquals(6, rr.getLineNumber());
        assertEquals(null, rr.readLine());
        rr.close();
    }

    private LineReadable create(final String in) {
        final Readable readable = new StringReader(in);
        return new LineReadable(readable);
    }

    private static class SkipLineFilter implements LineFilter {
        @Override
        public boolean accept(final Line line) {
            if ("".equals(line.getBody())) {
                return false;
            }
            return true;
        }
    }

}
