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

import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class Rfc4180ElementReaderTest extends ElementReaderTest {

    @Override
    protected ElementReader constructTest1Reader() {
        final InputStreamReader reader = new InputStreamReader(
                BeanCsvReaderTest.getResourceAsStream("-1", "tsv"),
                Charset.forName("UTF-8"));
        final Rfc4180Reader csvReader = new Rfc4180Reader();
        csvReader.setElementSeparator(CsvSetting.TAB);
        csvReader.setQuoteMark(CsvSetting.DOUBLE_QUOTE);
        csvReader.open(reader);
        return csvReader;
    }

}
