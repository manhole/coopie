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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

public class PoiLearningTest {

    /*
     * POIの行/列範囲取得方法を確認する。
     */
    @Test
    public void range() throws Throwable {
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1", "xls");
        final HSSFWorkbook book = new HSSFWorkbook(is);
        assertEquals("we have 3 sheets", 3, book.getNumberOfSheets());

        {
            final HSSFSheet sheet = book.getSheetAt(0);
            assertEquals("Sheet1", book.getSheetName(0));
            // 4行目まであるので3が帰る
            assertEquals(3, sheet.getLastRowNum());

            {
                final HSSFRow row = sheet.getRow(0);
                // C列まであるので3が帰る
                assertEquals(3, row.getLastCellNum());
                assertEquals("aaa", row.getCell(0).toString());
                assertEquals("ccc", row.getCell(1).toString());
                assertEquals("bbb", row.getCell(2).toString());
                // 無いセルをgetCellするとnullが帰る
                assertEquals(null, row.getCell(3));
                assertEquals(null, row.getCell(4));
            }
        }
        {
            final HSSFSheet sheet = book.getSheetAt(1);
            assertEquals("Sheet2", book.getSheetName(1));
            assertEquals(0, sheet.getLastRowNum());

            // 無い行をgetRowするとnullが帰る
            assertEquals(null, sheet.getRow(0));
            assertEquals(null, sheet.getRow(1));
        }
        {
            final HSSFSheet sheet = book.getSheetAt(2);
            assertEquals("Sheet3", book.getSheetName(2));
            assertEquals(0, sheet.getLastRowNum());
        }
        try {
            book.getSheetAt(3);
            fail("無いシートを取得すると例外になる");
        } catch (final IllegalArgumentException e) {
            /*
             * poi 3.1ではIndexOutOfBoundsExceptionだったが、
             * poi 3.9ではIllegalArgumentExceptionが投げられるようになった。
             */
        }
    }

}
