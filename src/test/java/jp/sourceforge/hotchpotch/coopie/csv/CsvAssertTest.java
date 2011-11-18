package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.fail;
import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;

public class CsvAssertTest extends CsvAssert {

    private static final Logger logger = LoggerFactory.getLogger();

    private final CsvAssert csvAssert_ = new CsvAssert();

    @Test
    public void array() {
        csvAssert_.assertArrayEquals(new String[] { "a", "b" }, new String[] {
                "a", "b" });
        csvAssert_.assertArrayEquals(null, null);

        try {
            csvAssert_.assertArrayEquals(new String[] { "a", "b" },
                    new String[] { "a" });
            fail();
        } catch (final CsvAssertionError e) {
            logger.debug(e.getMessage());
        }
        try {
            csvAssert_.assertArrayEquals(new String[] { "a", "b" },
                    new String[] { "a", "c" });
            fail();
        } catch (final CsvAssertionError e) {
            logger.debug(e.getMessage());
        }
        try {
            csvAssert_.assertArrayEquals(null, new String[] { "a", "c" });
            fail();
        } catch (final CsvAssertionError e) {
            logger.debug(e.getMessage());
        }
        try {
            csvAssert_.assertArrayEquals(new String[] { "a", "b" }, null);
            fail();
        } catch (final CsvAssertionError e) {
            logger.debug(e.getMessage());
        }
    }

}
