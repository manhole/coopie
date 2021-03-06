/*
 * Copyright 2010 manhole
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;

import jp.sourceforge.hotchpotch.coopie.util.ByteSize.ToStringMode;
import jp.sourceforge.hotchpotch.coopie.util.ByteSizeUnits.BaseType;

import org.junit.Test;

public class ByteSizeTest {

    @Test
    public void asByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(123L);

        // ## Act ##
        // ## Assert ##
        assertEquals("123", byteSize.toString());
        assertEquals("123", byteSize.toHumanReadableString());
    }

    @Test
    public void asKiloBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(543210L);

        // ## Act ##
        // ## Assert ##
        // (543,210 / 1024) => 530.478515625
        assertEquals("530.48 KiB (543,210)", byteSize.toString());
        assertEquals("530.48 KiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asKiloByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(543210L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        // (543,210 / 1000) => 543.21
        assertEquals("543.21 kB (543,210)", byteSize.toString());
        assertEquals("543.21 kB", byteSize.toHumanReadableString());
    }

    @Test
    public void asKiloBinary2() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(1024);

        // ## Act ##
        // ## Assert ##
        assertEquals("1.00 KiB (1,024)", byteSize.toString());
        assertEquals("1.00 KiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asKiloByte2() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(1000L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("1.00 kB (1,000)", byteSize.toString());
        assertEquals("1.00 kB", byteSize.toHumanReadableString());
    }

    @Test
    public void asMegaBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(76543210L);

        // ## Act ##
        // ## Assert ##
        // 72.99729347229004
        assertEquals("73.00 MiB (76,543,210)", byteSize.toString());
        assertEquals("73.00 MiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asMegaByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(76543210L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("76.54 MB (76,543,210)", byteSize.toString());
        assertEquals("76.54 MB", byteSize.toHumanReadableString());
    }

    @Test
    public void asGigaBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(9876543210L);

        // ## Act ##
        // ## Assert ##
        // 9.198247650638223
        assertEquals("9.20 GiB (9,876,543,210)", byteSize.toString());
        assertEquals("9.20 GiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asGigaByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(9876543210L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("9.88 GB (9,876,543,210)", byteSize.toString());
        assertEquals("9.88 GB", byteSize.toHumanReadableString());
    }

    @Test
    public void asTeraBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(98765432101234L);

        // ## Act ##
        // ## Assert ##
        // 89.82663721438621
        assertEquals("89.83 TiB (98,765,432,101,234)", byteSize.toString());
        assertEquals("89.83 TiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asTeraByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(98765432101234L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("98.77 TB (98,765,432,101,234)", byteSize.toString());
        assertEquals("98.77 TB", byteSize.toHumanReadableString());
    }

    @Test
    public void asPetaBinary() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(1298765432101234L);

        // ## Act ##
        // ## Assert ##
        // 1.153535429044824
        assertEquals("1.15 PiB (1,298,765,432,101,234)", byteSize.toString());
        assertEquals("1.15 PiB", byteSize.toHumanReadableString());
    }

    @Test
    public void asPetaByte() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(1298765432101234L);

        // ## Act ##
        byteSize.setBaseType(BaseType.DECIMAL);

        // ## Assert ##
        assertEquals("1.30 PB (1,298,765,432,101,234)", byteSize.toString());
        assertEquals("1.30 PB", byteSize.toHumanReadableString());
    }

    @Test
    public void toStringMode() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(76543211L);

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

    /*
     * 少数部が不要なケース
     */
    @Test
    public void customFormat1() throws Throwable {
        // ## Arrange ##
        final ByteSize byteSize = ByteSize.create(76543211L);

        // ## Act ##
        class CustomByteSizeFormatter1 implements ToStringMode {
            private final NumberFormat format_ = NumberFormat.getNumberInstance();

            public CustomByteSizeFormatter1() {
                format_.setRoundingMode(RoundingMode.HALF_UP);
                format_.setMinimumFractionDigits(0);
                format_.setMaximumFractionDigits(0);
            }

            @Override
            public String toString(final ByteSize byteSize) {
                final long size = byteSize.getSize();
                final ByteSizeUnit unit = ByteSizeUnits.detectUnit(byteSize);
                final StringBuilder sb = new StringBuilder();
                sb.append(unit.format(size, format_));
                sb.append(unit.getUnitLabel());
                return sb.toString();
            }
        }

        byteSize.setToStringMode(new CustomByteSizeFormatter1());

        // ## Assert ##
        assertThat(byteSize.toHumanReadableString(), is("73.00 MiB"));
        assertThat(byteSize.toString(), is("73MiB"));
    }

    @Test
    public void fromInputStream() {
        {
            final ByteSize size = ByteSize.create(new ByteArrayInputStream(new byte[10559]));
            assertThat(size.getSize(), is(10559L));
        }
        {
            final ByteSize size = ByteSize.create(new ByteArrayInputStream(new byte[0]));
            assertThat(size.getSize(), is(0L));
        }
        {
            final ByteSize size = ByteSize.create(new ByteArrayInputStream(new byte[1]));
            assertThat(size.getSize(), is(1L));
        }
    }

}
