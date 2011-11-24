package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractFixedLengthLayout.SimpleFixedLengthColumnDesc;

public class FixedLengthReaderTest extends ElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");
        final FixedLengthColumnDesc[] columns = new FixedLengthColumnDesc[] {
                col("a", 0, 5), col("b", 5, 12), col("c", 12, 20) };

        // ## Act ##
        final FixedLengthReader reader = new FixedLengthReader(
                new InputStreamReader(is, Charset.forName("UTF-8")), columns);
        return reader;
    }

    private SimpleFixedLengthColumnDesc col(final String name, final int begin,
            final int end) {
        return new AbstractFixedLengthLayout.SimpleFixedLengthColumnDesc(name,
                begin, end);
    }

}
