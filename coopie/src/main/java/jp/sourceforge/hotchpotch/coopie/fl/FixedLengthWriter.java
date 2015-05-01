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

import java.io.Closeable;
import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting;
import jp.sourceforge.hotchpotch.coopie.csv.ElementWriter;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.IORuntimeException;
import jp.sourceforge.hotchpotch.coopie.util.Text;

public class FixedLengthWriter implements ElementWriter {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private Appendable appendable_;
    private final FixedLengthElementDesc[] elementDescs_;
    private String lineSeparator_ = CsvSetting.CRLF;

    public FixedLengthWriter(final FixedLengthElementDesc[] columns) {
        elementDescs_ = columns;
    }

    public void open(final Appendable appendable) {
        appendable_ = appendable;
        closed_ = false;
    }

    @Override
    public void writeRecord(final String[] line) {
        final int len = Math.min(line.length, elementDescs_.length);
        try {
            for (int i = 0; i < len; i++) {
                final String s = line[i];
                final FixedLengthElementDesc elementDesc = elementDescs_[i];
                elementDesc.write(s, appendable_);
            }
            appendable_.append(getLineSeparator());
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
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

    public String getLineSeparator() {
        if (Text.isEmpty(lineSeparator_)) {
            lineSeparator_ = CsvSetting.CRLF;
        }
        return lineSeparator_;
    }

    public void setLineSeparator(final String lineSeparator) {
        lineSeparator_ = lineSeparator;
    }

}
