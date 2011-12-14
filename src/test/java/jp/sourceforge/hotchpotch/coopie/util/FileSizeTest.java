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
    }

    @Test
    public void asKiloByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(543210L);

        // ## Act ##
        // ## Assert ##
        assertEquals("530.48 KB (543,210)", fileSize.toString());
    }

    @Test
    public void asMegaByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(76543210L);

        // ## Act ##
        // ## Assert ##
        // 72.99729347229004
        assertEquals("73.00 MB (76,543,210)", fileSize.toString());
    }

    @Test
    public void asGigaByte() throws Throwable {
        // ## Arrange ##
        final FileSize fileSize = new FileSize(9876543210L);

        // ## Act ##
        // ## Assert ##
        // 9.198247650638223
        assertEquals("9.20 GB (9,876,543,210)", fileSize.toString());
    }

}
