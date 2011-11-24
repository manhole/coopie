package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import jp.sourceforge.hotchpotch.coopie.csv.ElementReader;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReaderTest;

public class FixedLengthReaderTest extends ElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() {
        // ## Arrange ##
        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream(
                "-1", "tsv");
        final FixedLengthColumnDesc[] columns = new FixedLengthColumnDesc[] {
                col(0, 5), col(5, 12), col(12, 20) };

        // ## Act ##
        final FixedLengthReader reader = new FixedLengthReader(
                new InputStreamReader(is, Charset.forName("UTF-8")), columns);
        return reader;
    }

    private FixedLengthColumnDesc col(final int begin, final int end) {
        return new AbstractFixedLengthLayout.SimpleFixedLengthColumnDesc(begin,
                end);
    }

}
