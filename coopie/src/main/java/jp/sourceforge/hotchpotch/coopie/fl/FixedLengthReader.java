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

package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.IOException;
import java.util.Iterator;

import jp.sourceforge.hotchpotch.coopie.csv.DefaultLineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.ElementParserContext;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReader;
import jp.sourceforge.hotchpotch.coopie.csv.LineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.IORuntimeException;
import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineImpl;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public class FixedLengthReader implements ElementReader {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private LineReader reader_;
    private final FixedLengthElementDesc[] elementDescs_;
    private int lineNo_;
    private final Line line_ = new LineImpl();
    private LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler.getInstance();

    private final ElementParserContext parserContext_ = FixedLengthParserContext.getInstance();

    public FixedLengthReader(final FixedLengthElementDesc[] columns) {
        elementDescs_ = columns;
    }

    public void open(final Readable readable) {
        reader_ = new LineReader(readable);
        closed_ = false;
    }

    @Override
    public int getRecordNumber() {
        return lineNo_;
    }

    @Override
    public int getLineNumber() {
        return lineNo_;
    }

    @Override
    public String[] readRecord() {
        try {
            final Line line = readLine();
            if (line == null) {
                return null;
            }

            final String body = line.getBody();
            final String[] record = new String[elementDescs_.length];
            for (int i = 0; i < elementDescs_.length; i++) {
                final FixedLengthElementDesc elementDesc = elementDescs_[i];
                final String elem = elementDesc.read(body);
                record[i] = elem;
            }
            lineNo_++;
            return record;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected Line readLine() throws IOException {
        while (true) {
            final Line line = lineReaderHandler_.readLine(reader_, line_);
            if (line == null) {
                return null;
            }
            if (!lineReaderHandler_.acceptLine(line, parserContext_)) {
                continue;
            }
            return line;
        }
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        CloseableUtil.closeNoException(reader_);
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

    @Override
    public Iterator<String[]> iterator() {
        // TODO
        throw new UnsupportedOperationException("iterator");
    }

    private static class FixedLengthParserContext implements ElementParserContext {

        private static final ElementParserContext INSTANCE = new FixedLengthParserContext();

        public static ElementParserContext getInstance() {
            return INSTANCE;
        }

        /**
         * 固定長ファイルでは、行をまたがる要素はあり得ないため、
         * 常にfalseを返す。
         */
        @Override
        public boolean isInElement() {
            return false;
        }

    }

}
