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

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public abstract class ElementReaderTest {

    protected abstract ElementReader constructTest1Reader() throws Throwable;

    @Test
    public void assertRead1() throws Throwable {
        // ## Arrange ##
        final ElementReader reader = constructTest1Reader();

        // ## Act ##
        // ## Assert ##
        /*
         * 初期値は"0"。
         * 1行目を読み終えたら"1"。
         */
        assertEquals(0, reader.getRecordNumber());
        assertEquals(0, reader.getLineNumber());
        assertArrayEquals(a("aaa", "ccc", "bbb"), reader.readRecord());
        assertEquals(1, reader.getRecordNumber());
        assertEquals(1, reader.getLineNumber());
        assertArrayEquals(a("あ1", "う1", "い1"), reader.readRecord());
        assertEquals(2, reader.getRecordNumber());
        assertEquals(2, reader.getLineNumber());
        assertArrayEquals(a("あ2", "う2", "い2"), reader.readRecord());
        assertEquals(3, reader.getRecordNumber());
        assertEquals(3, reader.getLineNumber());
        assertArrayEquals(a("あ3", "う3", "い3"), reader.readRecord());
        assertEquals(4, reader.getRecordNumber());
        assertEquals(4, reader.getLineNumber());
        assertNull(reader.readRecord());
        /*
         * 最後まで読んだ後はカウントアップしない
         */
        assertEquals(4, reader.getRecordNumber());
        assertEquals(4, reader.getRecordNumber());
        assertEquals(4, reader.getLineNumber());

        reader.close();
    }

}
