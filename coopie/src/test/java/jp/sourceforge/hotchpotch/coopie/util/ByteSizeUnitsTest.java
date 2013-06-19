package jp.sourceforge.hotchpotch.coopie.util;

import static org.junit.Assert.assertEquals;
import jp.sourceforge.hotchpotch.coopie.util.ByteSizeUnits.BaseType;

import org.junit.Test;

public class ByteSizeUnitsTest {

    @Test
    public void asMegaByte() throws Throwable {
        // ## Arrange ##
        // ## Act ##
        final ByteSize byteSize = ByteSizeUnits.MB.multiply(12);
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("12.00 MB (12,000,000)", byteSize.toString());
        assertEquals("12.00 MB", byteSize.toHumanReadableString());
    }

}
