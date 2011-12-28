package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileSizeTest {

    @Test
    public void asByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(123L);

        // ## Act ##
        // ## Assert ##
        assertEquals("123", fileSize.toString());
        assertEquals("123", fileSize.toHumanReadableString());
    }

    @Test
    public void asKiloByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(543210L);

        // ## Act ##
        // ## Assert ##
        assertEquals("530.48 KB (543,210)", fileSize.toString());
        assertEquals("530.48 KB", fileSize.toHumanReadableString());
    }

    @Test
    public void asMegaByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(76543210L);

        // ## Act ##
        // ## Assert ##
        // 72.99729347229004
        assertEquals("73.00 MB (76,543,210)", fileSize.toString());
        assertEquals("73.00 MB", fileSize.toHumanReadableString());
    }

    @Test
    public void asGigaByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(9876543210L);

        // ## Act ##
        // ## Assert ##
        // 9.198247650638223
        assertEquals("9.20 GB (9,876,543,210)", fileSize.toString());
        assertEquals("9.20 GB", fileSize.toHumanReadableString());
    }

    @Test
    public void asTeraByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(98765432101234L);

        // ## Act ##
        // ## Assert ##
        // 89.82663721438621
        assertEquals("89.83 TB (98,765,432,101,234)", fileSize.toString());
        assertEquals("89.83 TB", fileSize.toHumanReadableString());
    }

}
