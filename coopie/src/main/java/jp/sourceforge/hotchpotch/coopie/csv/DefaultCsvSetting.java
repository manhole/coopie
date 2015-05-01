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

import jp.sourceforge.hotchpotch.coopie.util.Text;

public class DefaultCsvSetting implements CsvSetting {

    /**
     * 要素区切り文字。
     * 未指定の場合はタブです。
     */
    private char elementSeparator_ = TAB;

    /**
     * 要素をクォートする文字。
     * 未指定の場合はダブルクォート(二重引用符)です。
     */
    private char quoteMark_ = DOUBLE_QUOTE;

    /**
     * 改行文字。
     * 未指定の場合はCRLFです。
     *
     * CsvWriterを使う場合は、何らかの値が設定されている必要があります。
     * (CRLFのままでもOKです)
     *
     * CsvReaderを使う場合は、未設定のままで構いません。
     */
    private String lineSeparator_ = CRLF;

    private QuoteMode quoteMode_ = QuoteMode.ALWAYS_EXCEPT_NULL;

    @Override
    public String getLineSeparator() {
        if (Text.isEmpty(lineSeparator_)) {
            lineSeparator_ = CRLF;
        }
        return lineSeparator_;
    }

    @Override
    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

    @Override
    public char getQuoteMark() {
        return quoteMark_;
    }

    @Override
    public void setQuoteMark(final char quoteMark) {
        quoteMark_ = quoteMark;
    }

    @Override
    public char getElementSeparator() {
        return elementSeparator_;
    }

    @Override
    public void setElementSeparator(final char elementSeparator) {
        elementSeparator_ = elementSeparator;
    }

    @Override
    public QuoteMode getQuoteMode() {
        return quoteMode_;
    }

    @Override
    public void setQuoteMode(final QuoteMode quoteMode) {
        quoteMode_ = quoteMode;
    }

}
