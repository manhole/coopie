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
