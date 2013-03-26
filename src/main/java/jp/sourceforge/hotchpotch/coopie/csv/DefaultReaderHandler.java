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

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public class DefaultReaderHandler implements ElementReaderHandler,
        LineReaderHandler, ElementEditor {

    private static final DefaultReaderHandler INSTANCE = new DefaultReaderHandler();

    public static DefaultReaderHandler getInstance() {
        return INSTANCE;
    }

    private final LineReaderHandler lineReaderHandler_ = DefaultLineReaderHandler
            .getInstance();
    private final ElementReaderHandler elementReaderHandler_ = DefaultElementReaderHandler
            .getInstance();
    private final ElementEditor elementEditor_ = ElementEditors.passThrough();

    @Override
    public boolean acceptLine(final Line line,
            final ElementParserContext parserContext) {
        return lineReaderHandler_.acceptLine(line, parserContext);
    }

    @Override
    public Line readLine(final LineReader lineReader, final Line reusableLine)
            throws IOException {
        return lineReaderHandler_.readLine(lineReader, reusableLine);
    }

    @Override
    public String[] readRecord(final ElementReader elementReader) {
        return elementReaderHandler_.readRecord(elementReader);
    }

    @Override
    public String edit(final String element) {
        return elementEditor_.edit(element);
    }

}
