package jp.sourceforge.hotchpotch.coopie.groovy

import static org.junit.Assert.assertEquals
import jp.sourceforge.hotchpotch.coopie.util.ByteSize
import jp.sourceforge.hotchpotch.coopie.util.ByteSizeUnits

import org.junit.Test

class ByteSizeUnitsTest {

    @Test
    public void gigaBinary() {
        // ## Arrange ##
        final ByteSize byteSize = ByteSizeUnits.GiB * 123

        // ## Act ##
        // ## Assert ##
        assertEquals("123.00 GiB (132,070,244,352)", byteSize.toString())
        assertEquals("123.00 GiB", byteSize.toHumanReadableString())
    }

    @Test
    public void gigaByte() {
        // ## Arrange ##
        final ByteSize byteSize = ByteSizeUnits.GB * 123

        // ## Act ##
        // ## Assert ##
        assertEquals("123.00 GB (123,000,000,000)", byteSize.toString())
        assertEquals("123.00 GB", byteSize.toHumanReadableString())
    }
}
