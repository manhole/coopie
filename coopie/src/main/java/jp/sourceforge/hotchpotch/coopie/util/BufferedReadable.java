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

import java.io.Closeable;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Arrays;

public class BufferedReadable implements Closable {

    private static final char NULL_CHAR = '\u0000';
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;
    private final Readable readable_;
    private final CharBuffer charBuffer_;
    private int readSize_;
    private boolean eof_;

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    public BufferedReadable(final Readable readable) {
        this(readable, DEFAULT_BUFFER_SIZE);
    }

    public BufferedReadable(final Readable readable, final int bufferSize) {
        readable_ = readable;
        charBuffer_ = CharBuffer.allocate(bufferSize);
    }

    /**
     * 次の1文字を読み、ポインタを次へ進めます。
     * 次の文字が無い場合(最後まで読んだ場合)は、NULL_CHAR ('\u0000')を返します。
     * 
     * 当メソッドを続けて呼ぶと、1文字ずつ次を返します。
     * 
     * @return 次の1文字
     */
    public char readChar() throws IOException {
        if (isEof()) {
            return NULL_CHAR;
        }

        // bufferの最後まで使った or 初回
        if (charBuffer_.position() == readSize_) {
            charBuffer_.clear();
            readSize_ = readable_.read(charBuffer_);
            charBuffer_.clear();
        }
        if (readSize_ == -1) {
            eof_ = true;
            return NULL_CHAR;
        }

        // ポインタを次へ進める
        final char c = charBuffer_.get();
        return c;
    }

    /**
     * 次の1文字を返します。
     * 次の文字が無い場合(最後まで読んだ場合)は、NULL_CHAR ('\u0000')を返します。
     * 
     * {@link #readChar()}と異なり、ポインタを先へ進めません。
     * 
     * 当メソッドを続けて呼ぶと、同じ文字を返し続けます。
     * 
     * @return 次の1文字
     */
    public char peekChar() throws IOException {
        if (isEof()) {
            return NULL_CHAR;
        }

        if (charBuffer_.position() == readSize_) {
            /*
             * bufferから既にgetした分を捨てて、残りを左へ詰める
             */
            {
                int left = 0;
                int pos = charBuffer_.position();
                for (; pos < readSize_; left++, pos++) {
                    final char c = charBuffer_.get();
                    charBuffer_.put(left, c);
                }
                charBuffer_.position(left);
                readSize_ = readable_.read(charBuffer_) + left;
            }
            if (readSize_ == -1) {
                eof_ = true;
                return NULL_CHAR;
            }
            charBuffer_.clear();
        }

        // ポインタを次へ進めない
        final char c = charBuffer_.get(charBuffer_.position());
        return c;
    }

    public char[] readChars() throws IOException {
        if (eof_) {
            return null;
        }

        // bufferの最後まで使った or 初回
        if (charBuffer_.position() == readSize_) {
            charBuffer_.position(0);
            readSize_ = readable_.read(charBuffer_);
            if (readSize_ == -1) {
                eof_ = true;
                return null;
            }
            final char[] array = charBuffer_.array();
            if (readSize_ == array.length) {
                return array;
            }
            final char[] lastRecord = Arrays.copyOfRange(array, 0, readSize_);
            return lastRecord;
        }

        final char[] array = charBuffer_.array();
        final char[] chars = Arrays.copyOfRange(array, charBuffer_.position(), readSize_);
        readSize_ = charBuffer_.position();
        return chars;
    }

    public boolean isEof() {
        return eof_;
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        if (readable_ instanceof Closeable) {
            final Closeable closeable = Closeable.class.cast(readable_);
            CloseableUtil.closeNoException(closeable);
        }
    }

}
