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

import java.util.Map;

public class MapCsvLayout<PROP> extends AbstractMapCsvLayout<PROP> {

    private final CsvSetting csvSetting_;

    public MapCsvLayout() {
        csvSetting_ = new DefaultCsvSetting();
    }

    public void setElementSeparator(final char elementSeparator) {
        csvSetting_.setElementSeparator(elementSeparator);
    }

    public void setLineSeparator(final String lineSeparator) {
        csvSetting_.setLineSeparator(lineSeparator);
    }

    public void setQuoteMark(final char quoteMark) {
        csvSetting_.setQuoteMark(quoteMark);
    }

    public void setQuoteMode(final QuoteMode quoteMode) {
        csvSetting_.setQuoteMode(quoteMode);
    }

    protected ElementInOut createElementInOut() {
        final CsvElementInOut a = new CsvElementInOut(csvSetting_);
        a.setLineReaderHandler(getLineReaderHandler());
        return a;
    }

    public RecordInOut<Map<String, PROP>> build() {
        prepareBuild();

        final CsvRecordInOut<Map<String, PROP>> obj = new CsvRecordInOut<>();
        obj.setRecordDesc(getRecordDesc());
        obj.setWithHeader(isWithHeader());
        obj.setElementInOut(createElementInOut());
        obj.setElementReaderHandler(getElementReaderHandler());
        obj.setElementEditor(getElementEditor());
        return obj;
    }

}
