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

public class CsvElementInOut implements ElementInOut {

    private final CsvSetting csvSetting_;
    private LineReaderHandler lineReaderHandler_;

    public CsvElementInOut(final CsvSetting csvSetting) {
        if (csvSetting == null) {
            throw new NullPointerException("csvSetting");
        }
        csvSetting_ = csvSetting;
    }

    @Override
    public ElementWriter openWriter(final Appendable appendable) {
        final Rfc4180Writer writer = createWriter();
        writer.open(appendable);
        return writer;
    }

    @Override
    public ElementReader openReader(final Readable readable) {
        final Rfc4180Reader reader = createReader();
        reader.open(readable);
        return reader;
    }

    protected Rfc4180Writer createWriter() {
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setElementSeparator(csvSetting_.getElementSeparator());
        writer.setLineSeparator(csvSetting_.getLineSeparator());
        writer.setQuoteMark(csvSetting_.getQuoteMark());
        writer.setQuoteMode(csvSetting_.getQuoteMode());
        return writer;
    }

    protected Rfc4180Reader createReader() {
        final Rfc4180Reader reader = new Rfc4180Reader();
        reader.setElementSeparator(csvSetting_.getElementSeparator());
        reader.setQuoteMark(csvSetting_.getQuoteMark());
        if (lineReaderHandler_ != null) {
            reader.setLineReaderHandler(lineReaderHandler_);
        }
        return reader;
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

}
