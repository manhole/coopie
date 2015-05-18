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

public class BeanCsvLayout<BEAN> extends AbstractBeanCsvLayout<BEAN> {

    private final CsvSetting csvSetting_;

    public static <BEAN> BeanCsvLayout<BEAN> getInstance(final Class<BEAN> beanClass) {
        final BeanCsvLayout<BEAN> instance = new BeanCsvLayout<BEAN>(beanClass);
        return instance;
    }

    public BeanCsvLayout(final Class<BEAN> beanClass) {
        super(beanClass);
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

    public RecordInOut<BEAN> build() {
        prepareBuild();

        final CsvRecordInOut<BEAN> obj = new CsvRecordInOut<BEAN>();
        obj.setRecordDesc(getRecordDesc());
        obj.setWithHeader(isWithHeader());
        obj.setElementInOut(createElementInOut());
        obj.setElementReaderHandler(getElementReaderHandler());
        obj.setElementEditor(getElementEditor());
        return obj;
    }

}
