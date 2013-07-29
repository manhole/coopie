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
