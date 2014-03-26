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

package jp.sourceforge.hotchpotch.coopie.fl;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;
import org.t2framework.commons.util.ReaderUtil;

public class FixedLengthWriterTest {

    @Test
    public void test1() throws Throwable {
        // ## Arrange ##

        final FixedLengthElementDesc[] descs = new FixedLengthElementDesc[] { col(0, 5), col(5, 12), col(12, 20) };

        final StringWriter sw = new StringWriter();

        // ## Act ##
        final FixedLengthWriter writer = new FixedLengthWriter(descs);
        writer.open(sw);
        writer.writeRecord(a("aaa", "ccc", "bbb"));
        writer.writeRecord(a("あ1", "う1", "い1"));
        writer.writeRecord(a("あ2", "う2", "い2"));
        writer.writeRecord(a("あ3", "う3", "い3"));
        writer.close();

        // ## Assert ##
        final String actual = sw.toString();

        final InputStream is = BeanFixedLengthReaderTest.getResourceAsStream("-1", "tsv");
        final String expected = ReaderUtil.readText(new InputStreamReader(is, "UTF-8"));
        assertEquals(expected, actual);
    }

    private FixedLengthElementDesc col(final int begin, final int end) {
        return new AbstractFixedLengthLayout.SimpleFixedLengthElementDesc(begin, end);
    }

}
