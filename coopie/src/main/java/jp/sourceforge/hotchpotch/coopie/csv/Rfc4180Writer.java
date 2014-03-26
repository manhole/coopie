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

import java.io.Closeable;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;

import org.t2framework.commons.exception.IORuntimeException;

/**
 * CSV Writer
 * 
 * http://www.rfc-editor.org/rfc/rfc4180.txt
 * http://www.kasai.fm/wiki/rfc4180jp (日本語訳)
 * 
 * @author manhole
 */
public class Rfc4180Writer implements ElementWriter {

    private static final char CR = CsvSetting.CR;
    private static final char LF = CsvSetting.LF;

    protected boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private Appendable appendable_;

    private char elementSeparator_ = CsvSetting.COMMA;
    private char quoteMark_ = CsvSetting.DOUBLE_QUOTE;
    private String lineSeparator_ = CsvSetting.CRLF;
    private QuoteMode quoteMode_ = QuoteMode.MINIMUM;
    private ElementWriteStrategy elementWriteStrategy_;

    public void open(final Appendable appendable) {
        appendable_ = appendable;
        closed_ = false;
        elementWriteStrategy_ = getElementWriteStrategy(quoteMode_);
    }

    @Override
    public void writeRecord(final String[] line) {
        try {
            for (int i = 0; i < line.length; i++) {
                final String elem = line[i];
                if (0 < i) {
                    appendable_.append(elementSeparator_);
                }
                writeElement(elem);
            }
            appendable_.append(lineSeparator_);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private void writeElement(final String elem) throws IOException {
        elementWriteStrategy_.writeElement(elem);
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (appendable_ instanceof Closeable) {
            final Closeable closeable = Closeable.class.cast(appendable_);
            CloseableUtil.closeNoException(closeable);
        }
    }

    public void setElementSeparator(final char elementSeparator) {
        elementSeparator_ = elementSeparator;
    }

    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

    public void setQuoteMark(final char quoteMark) {
        quoteMark_ = quoteMark;
    }

    public void setQuoteMode(final QuoteMode quoteMode) {
        quoteMode_ = quoteMode;
    }

    private ElementWriteStrategy getElementWriteStrategy(final QuoteMode quoteMode) {
        switch (quoteMode) {
        case ALWAYS:
            return new AlwaysStrategy();

        case ALWAYS_EXCEPT_NULL:
            return new AlwaysExceptNullStrategy();

        case MINIMUM:
            return new MinimumStrategy();

        default:
            throw new AssertionError();
        }
    }

    private abstract class ElementWriteStrategy {

        public abstract void writeElement(final String elem) throws IOException;

        protected void writeChars(final char[] chars) throws IOException {
            for (final char c : chars) {
                if (c == quoteMark_) {
                    // クォートをエスケープする
                    appendable_.append(quoteMark_);
                }
                appendable_.append(c);
            }
        }

    }

    private class AlwaysStrategy extends ElementWriteStrategy {

        @Override
        public void writeElement(final String elem) throws IOException {
            appendable_.append(quoteMark_);
            if (elem != null) {
                final char[] chars = elem.toCharArray();
                writeChars(chars);
            }
            appendable_.append(quoteMark_);
        }

    }

    private class AlwaysExceptNullStrategy extends ElementWriteStrategy {

        @Override
        public void writeElement(final String elem) throws IOException {
            if (elem == null) {
                return;
            }

            appendable_.append(quoteMark_);
            final char[] chars = elem.toCharArray();
            writeChars(chars);
            appendable_.append(quoteMark_);
        }

    }

    private class MinimumStrategy extends ElementWriteStrategy {

        @Override
        public void writeElement(final String elem) throws IOException {
            if (elem == null) {
                return;
            }

            /*
             * クォートを最小限にするため、先に1度走査する。
             */
            final char[] chars = elem.toCharArray();
            boolean shouldQuote = false;
            boolean hasQuoteChar = false;
            for (final char c : chars) {
                if (!shouldQuote) {
                    if (c == elementSeparator_) {
                        shouldQuote = true;
                    } else if (c == CR) {
                        shouldQuote = true;
                    } else if (c == LF) {
                        shouldQuote = true;
                    }
                }
                if (!hasQuoteChar && c == quoteMark_) {
                    shouldQuote = true;
                    hasQuoteChar = true;
                    break;
                }
            }

            if (shouldQuote) {
                // クォートが必要な場合
                appendable_.append(quoteMark_);
                if (hasQuoteChar) {
                    // 要素の中にクォート文字が含まれている場合は、1文字ずつチェックしながら出力する
                    writeChars(chars);
                } else {
                    // 要素の中にクォート文字が含まれていない場合は、そのまま出力する
                    appendable_.append(elem);
                }
                appendable_.append(quoteMark_);
            } else {
                // クォートが不要な場合は、要素をそのまま出力する
                appendable_.append(elem);
            }
        }

    }

}
