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

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.FailureProtection;

public class BeanCsvLayout<BEAN> extends AbstractBeanCsvLayout<BEAN> implements
        RecordInOut<BEAN> {

    private final CsvSetting csvSetting_;

    public static <BEAN> BeanCsvLayout<BEAN> getInstance(
            final Class<BEAN> beanClass) {
        final BeanCsvLayout<BEAN> instance = new BeanCsvLayout<BEAN>(beanClass);
        return instance;
    }

    public BeanCsvLayout(final Class<BEAN> beanClass) {
        super(beanClass);
        csvSetting_ = new DefaultCsvSetting();
    }

    @Override
    public RecordReader<BEAN> openReader(final Readable readable) {
        if (readable == null) {
            throw new NullPointerException("readable");
        }

        prepareOpen();
        final DefaultRecordReader<BEAN> r = new DefaultRecordReader<BEAN>(
                getRecordDesc());
        r.setWithHeader(isWithHeader());
        r.setElementInOut(createElementInOut());
        r.setElementReaderHandler(getElementReaderHandler());
        r.setElementEditor(getElementEditor());

        new FailureProtection<RuntimeException>() {

            @Override
            protected void protect() {
                r.open(readable);
            }

            @Override
            protected void rescue() {
                CloseableUtil.closeNoException(r);
            }

        }.execute();
        return r;
    }

    @Override
    public RecordWriter<BEAN> openWriter(final Appendable appendable) {
        if (appendable == null) {
            throw new NullPointerException("appendable");
        }

        prepareOpen();
        final DefaultRecordWriter<BEAN> w = new DefaultRecordWriter<BEAN>(
                getRecordDesc());
        w.setWithHeader(isWithHeader());
        w.setElementInOut(createElementInOut());
        // TODO openで例外時にcloseすること
        w.open(appendable);
        return w;
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

}