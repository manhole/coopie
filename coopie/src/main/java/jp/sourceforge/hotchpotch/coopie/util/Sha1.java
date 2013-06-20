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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1 {

    private final FileOperation files_ = new FileOperation();

    public String digest(final File file) throws IOException {
        final MessageDigest md = getMessageDigest();
        final BufferedInputStream is = files_.openBufferedInputStream(file);
        try {
            final byte[] bytes = new byte[files_.getBufferSize()];
            while (true) {
                final int len = is.read(bytes);
                if (len == -1) {
                    break;
                }
                md.update(bytes, 0, len);
            }
        } finally {
            CloseableUtil.closeNoException(is);
        }

        final byte[] digest = md.digest();
        return toHexString(digest);
    }

    private MessageDigest getMessageDigest() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            return md;
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String toHexString(final byte[] digest) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : digest) {
            toHexString(b, sb);
        }
        final String s = sb.toString();
        return s;
    }

    private void toHexString(final byte b, final StringBuilder sb) {
        final int i = byteToInt(b);
        final String s = Integer.toHexString(i);
        if (s.length() == 1) {
            sb.append('0');
        }
        sb.append(s);
    }

    private int byteToInt(final byte b) {
        return b & 0xFF;
    }

}
