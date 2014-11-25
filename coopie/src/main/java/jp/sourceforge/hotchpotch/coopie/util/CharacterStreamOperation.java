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
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

public class CharacterStreamOperation {

    private static final int K = 1024;
    private static final int DEFAULT_BUFF_SIZE = K * 8;

    private int bufferSize_ = DEFAULT_BUFF_SIZE;

    public void pipe(final Readable in, final Appendable out) throws IOException {
        final CharBuffer buff = CharBuffer.allocate(bufferSize_);
        final char[] backing = buff.array();
        while (true) {
            final int len = in.read(buff);
            if (-1 == len) {
                break;
            }

            buff.rewind();

            // いずれの方法でもOK
            //out.append(buff.subSequence(0, len));
            //out.append(buff, 0, len);
            for (int i = 0; i < len; i++) {
                out.append(backing[i]);
            }
        }
    }

    public void pipe(final Reader in, final Writer out) throws IOException {
        final char[] buf = new char[bufferSize_];
        for (int len = 0; (len = in.read(buf, 0, buf.length)) != -1;) {
            out.write(buf, 0, len);
        }
    }

    public void setBufferSize(final int bufferSize) {
        bufferSize_ = bufferSize;
    }

}
