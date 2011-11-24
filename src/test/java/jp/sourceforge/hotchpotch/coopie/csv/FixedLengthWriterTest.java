package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractFixedLengthLayout.SimpleFixedLengthColumnDesc;

import org.junit.Test;
import org.t2framework.commons.util.ReaderUtil;

public class FixedLengthWriterTest {

    @Test
    public void test1() throws Throwable {
        // ## Arrange ##

        final FixedLengthColumnDesc[] columns = new FixedLengthColumnDesc[] {
                col("a", 0, 5), col("b", 5, 12), col("c", 12, 20) };

        final StringWriter sw = new StringWriter();

        // ## Act ##
        final FixedLengthWriter writer = new FixedLengthWriter(sw, columns);
        writer.writeRecord(a("aaa", "ccc", "bbb"));
        writer.writeRecord(a("あ1", "う1", "い1"));
        writer.writeRecord(a("あ2", "う2", "い2"));
        writer.writeRecord(a("あ3", "う3", "い3"));
        writer.close();

        // ## Assert ##
        final String actual = sw.toString();

        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is,
                "UTF-8"));
        assertEquals(expected, actual);
    }

    private SimpleFixedLengthColumnDesc col(final String name, final int begin,
            final int end) {
        return new AbstractFixedLengthLayout.SimpleFixedLengthColumnDesc(name,
                begin, end);
    }

}
