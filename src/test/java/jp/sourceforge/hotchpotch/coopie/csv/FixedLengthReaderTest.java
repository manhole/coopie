package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractFixedLengthLayout.SimpleFixedLengthColumn;

import org.junit.Test;

public class FixedLengthReaderTest {

    @Test
    public void test1() throws Throwable {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");
        final FixedLengthColumn[] columns = new FixedLengthColumn[] {
                col("a", 0, 5), col("b", 5, 12), col("c", 12, 20) };

        // ## Act ##
        final FixedLengthReader reader = new FixedLengthReader(
                new InputStreamReader(is, Charset.forName("UTF-8")), columns);

        // ## Assert ##
        assertArrayEquals(a("aaa", "ccc", "bbb"), reader.readRecord());
        assertArrayEquals(a("あ1", "う1", "い1"), reader.readRecord());
        assertArrayEquals(a("あ2", "う2", "い2"), reader.readRecord());
        assertArrayEquals(a("あ3", "う3", "い3"), reader.readRecord());
        assertNull(reader.readRecord());

        reader.close();
    }

    private SimpleFixedLengthColumn col(final String name, final int begin,
            final int end) {
        return new AbstractFixedLengthLayout.SimpleFixedLengthColumn(name,
                begin, end);
    }

}
