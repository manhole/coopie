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

package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;

public class FilterLineReader implements LineReadable {

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private final LineReadable lineReader_;
    private final LineFilter lineFilter_;

    public FilterLineReader(final LineReadable lineReader,
            final LineFilter lineFilter) {
        lineReader_ = lineReader;
        lineFilter_ = lineFilter;
    }

    @Override
    public int getLineNumber() {
        return lineReader_.getLineNumber();
    }

    @Override
    public Line readLine() throws IOException {
        while (true) {
            final Line line = lineReader_.readLine();
            if (line == null) {
                return null;
            }
            if (lineFilter_.accept(line)) {
                return line;
            }
        }
    }

    @Override
    public Line readLine(final Line reusableLine) throws IOException {
        Line line = reusableLine;
        while (true) {
            line = lineReader_.readLine(line);
            if (line == null) {
                return null;
            }
            if (lineFilter_.accept(line)) {
                return line;
            }
        }
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        CloseableUtil.closeNoException(lineReader_);
    }

}
