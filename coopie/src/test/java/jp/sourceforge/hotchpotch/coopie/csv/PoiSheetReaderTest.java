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
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;
import org.t2framework.commons.util.ResourceUtil;

public class PoiSheetReaderTest extends ElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() throws Throwable {
        final InputStream is = BeanCsvReaderTest.getResourceAsStream("-1", "xls");
        final HSSFWorkbook workbook = new HSSFWorkbook(is);
        final DefaultExcelReader.PoiSheetReader poiReader = new DefaultExcelReader.PoiSheetReader(workbook,
                workbook.getSheetAt(0));
        return poiReader;
    }

    @Test
    public void rfc1() throws Throwable {
        // ## Arrange ##
        final InputStream is = getResourceAsStream("-formula", "xlsx");
        final DefaultExcelReader.PoiReader poiReader = new DefaultExcelReader.PoiReader(is);
        poiReader.focusSheet(0);

        // ## Act ##
        // ## Assert ##
        assertThat(poiReader.readRecord(), is(a("1", "ABC", "cde", "ABCcde")));
        assertThat(poiReader.readRecord(), is(a("2", "12", "34", "1234")));
        assertThat(poiReader.readRecord(), is(a("3", "12", "34", "46")));
        assertThat(poiReader.readRecord(), is(a("4", "true", "false", "true")));
        assertThat(poiReader.readRecord(), is(a("5", "12.3", "34", "46.3")));

        poiReader.close();
    }

    static InputStream getResourceAsStream(final String suffix, final String ext) {
        return ResourceUtil.getResourceAsStream(PoiSheetReaderTest.class.getName() + suffix, ext);
    }

}
