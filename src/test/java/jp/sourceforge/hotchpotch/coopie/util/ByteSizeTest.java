package jp.sourceforge.hotchpotch.coopie.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;

import jp.sourceforge.hotchpotch.coopie.util.ByteSize.BaseType;

import org.junit.Test;

public class ByteSizeTest {

    @Test
    public void asByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(123L);

        // ## Act ##
        // ## Assert ##
        assertEquals("123", byteSize.toString());
        assertEquals("123", byteSize.toHumanReadableString());
    }

    @Test
    public void asKiloBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(543210L);

        // ## Act ##
        // ## Assert ##
        // (543,210 / 1024) => 530.478515625
        assertEquals("530.48 KiB (543,210)", byteSize.toString());
        assertEquals("530.48 KiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asKiloByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(543210L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        // (543,210 / 1000) => 543.21
        assertEquals("543.21 kB (543,210)", byteSize.toString());
        assertEquals("543.21 kB", byteSize.toHumanReadableString());
    }

    @Test
    public void asMegaBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(76543210L);

        // ## Act ##
        // ## Assert ##
        // 72.99729347229004
        assertEquals("73.00 MiB (76,543,210)", byteSize.toString());
        assertEquals("73.00 MiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asMegaByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(76543210L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("76.54 MB (76,543,210)", byteSize.toString());
        assertEquals("76.54 MB", byteSize.toHumanReadableString());
    }

    @Test
    public void asGigaBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(9876543210L);

        // ## Act ##
        // ## Assert ##
        // 9.198247650638223
        assertEquals("9.20 GiB (9,876,543,210)", byteSize.toString());
        assertEquals("9.20 GiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asGigaByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(9876543210L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("9.88 GB (9,876,543,210)", byteSize.toString());
        assertEquals("9.88 GB", byteSize.toHumanReadableString());
    }

    @Test
    public void asTeraBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(98765432101234L);

        // ## Act ##
        // ## Assert ##
        // 89.82663721438621
        assertEquals("89.83 TiB (98,765,432,101,234)", byteSize.toString());
        assertEquals("89.83 TiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asTeraByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(98765432101234L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("98.77 TB (98,765,432,101,234)", byteSize.toString());
        assertEquals("98.77 TB", byteSize.toHumanReadableString());
    }

    @Test
    public void toStringMode() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = new ByteSize(76543211L);

        // ## Act ##
        // ## Assert ##
        assertThat(byteSize.toString(), is("73.00 MiB (76,543,211)"));

        try {
            byteSize.setToStringMode(null);
            fail();
        } catch (final NullPointerException e) {
        }

        byteSize.setToStringMode(ByteSize.HUMAN_READABLE);
        assertThat(byteSize.toString(), is("73.00 MiB"));

        byteSize.setToStringMode(ByteSize.BYTE);
        assertThat(byteSize.toString(), is("76,543,211"));

        byteSize.setToStringMode(ByteSize.DETAIL);
        assertThat(byteSize.toString(), is("73.00 MiB (76,543,211)"));
    }

    @Test
    public void fromInputStream() {
        {
            final ByteSize size = ByteSize.create(new ByteArrayInputStream(
                    new byte[10559]));
            assertThat(size.getSize(), is(10559L));
        }
        {
            final ByteSize size = ByteSize.create(new ByteArrayInputStream(
                    new byte[0]));
            assertThat(size.getSize(), is(0L));
        }
        {
            final ByteSize size = ByteSize.create(new ByteArrayInputStream(
                    new byte[1]));
            assertThat(size.getSize(), is(1L));
        }
    }

}
