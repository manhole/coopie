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
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryStreamOperation {

    private static final int K = 1024;
    private static final int DEFAULT_BUFF_SIZE = K * 8;

    private int bufferSize_ = DEFAULT_BUFF_SIZE;

    public void pipe(final InputStream is, final OutputStream os) throws IOException {
        final byte[] buf = new byte[bufferSize_];
        for (int len = 0; (len = is.read(buf, 0, buf.length)) != -1;) {
            os.write(buf, 0, len);
        }
    }

    public void setBufferSize(final int bufferSize) {
        bufferSize_ = bufferSize;
    }

}
