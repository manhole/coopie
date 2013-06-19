package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ByteSizeUnitsTest {

    @Test
    public void megaBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSizeUnits.MiB.multiply(12);

        // ## Act ##
        // ## Assert ##
        assertEquals("12.00 MiB (12,582,912)", byteSize.toString());
        assertEquals("12.00 MiB", byteSize.toHumanReadableString());
    }

    @Test
    public void megaByte() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ByteSize byteSize = ByteSizeUnits.MB.multiply(12);

        // ## Assert ##
        assertEquals("12.00 MB (12,000,000)", byteSize.toString());
        assertEquals("12.00 MB", byteSize.toHumanReadableString());
    }

}
