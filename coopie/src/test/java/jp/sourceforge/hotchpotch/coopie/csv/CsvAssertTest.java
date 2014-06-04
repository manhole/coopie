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

package jp.sourceforge.hotchpotch.coopie.csv;

import static org.junit.Assert.fail;

import java.io.Reader;
import java.io.StringReader;

import jp.sourceforge.hotchpotch.coopie.logging.Logger;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.junit.Test;

public class CsvAssertTest extends CsvAssert {

    private static final Logger logger = LoggerFactory.getLogger();

    private final CsvAssert csvAssert_ = new CsvAssert();

    @Test
    public void csv() throws Throwable {
        csvAssert_.setElementSeparator(CsvSetting.COMMA);
        csvAssert_.assertCsvEquals((Reader) null, null);
        csvAssert_.assertCsvEquals(new StringReader("A,B\n" + "a,b"), new StringReader("A,B\n" + "a,b"));
        csvAssert_.assertCsvEquals(new StringReader("A,B\n" + "a,b"), new StringReader("B,A\n" + "b,a"));

        try {
            csvAssert_.assertCsvEquals(new StringReader("A,B\n" + "a,b"), new StringReader("B,A\n" + "b,A"));
            fail();
        } catch (final CsvAssertionError e) {
            logger.debug(e.getMessage());
        }
    }

    @Test
    public void array() {
        csvAssert_.assertArrayEquals(new String[] { "a", "b" }, new String[] { "a", "b" });
        csvAssert_.assertArrayEquals(null, null);

        try {
            csvAssert_.assertArrayEquals(new String[] { "a", "b" }, new String[] { "a" });
            fail();
        } catch (final CsvAssertionError e) {
            logger.debug(e.getMessage());
        }
        try {
            csvAssert_.assertArrayEquals(new String[] { "a", "b" }, new String[] { "a", "c" });
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
