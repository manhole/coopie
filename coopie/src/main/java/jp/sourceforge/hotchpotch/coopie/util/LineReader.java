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
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

public class LineReader implements LineReadable {

    private static final char CR = IOUtil.CR;
    private static final char LF = IOUtil.LF;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);
    private final Readable readable_;
    private LineSeparator lineSeparator_;
    // 改行文字を除いた、1行の文字列
    private String lineBody_;
    private int lineNumber_;
    private Deque<Line> pushback_;
    private boolean eof_;
    private final char[] buffer_;
    private int pos_;
    private int length_;
    private final CharBuffer charBuffer_;
    private int bodyStartPos_;
    private int bodyLength_;
    private StringBuilder bodyBuffer_;

    public LineReader(final Readable readable) {
        this(readable, DEFAULT_BUFFER_SIZE);
    }

    public LineReader(final Readable readable, final int bufferSize) {
        readable_ = readable;
        buffer_ = new char[bufferSize];
        charBuffer_ = CharBuffer.wrap(buffer_);
    }

    public String readLineBody() throws IOException {
        read0();
        return lineBody_;
    }

    @Override
    public Line readLine() throws IOException {
        final Line line = createLine();
        final Line read = readLine(line);
        return read;
    }

    @Override
    public Line readLine(final Line reusableLine) throws IOException {
        read0();
        if (lineBody_ == null) {
            return null;
        }

        reusableLine.reinit(lineBody_, lineNumber_, lineSeparator_);
        return reusableLine;
    }

    protected Line createLine() {
        return new LineImpl();
    }

    private void read0() throws IOException {
        if (pushback_ != null) {
            final Line line = pushback_.pop();
            lineBody_ = line.getBody();
            lineNumber_++;
            lineSeparator_ = line.getSeparator();
            if (pushback_.isEmpty()) {
                pushback_ = null;
            }
            return;
        }

        if (eof_) {
            lineBody_ = null;
            lineSeparator_ = null;
            return;
        }

        bodyBuffer_ = null;
        bodyStartPos_ = pos_;
        bodyLength_ = 0;
        LineSeparator sep = LineSeparator.NONE;

        // 改行文字まで or 文字列の最後まで読む
        read_loop: while (true) {
            readIfNecessary();
            if (eof_) {
                break;
            }

            while (pos_ < length_) {
                final char c = buffer_[pos_];
                pos_++;
                if (sep == LineSeparator.CR) {
                    if (c == LF) {
                        sep = LineSeparator.CRLF;
                    } else {
                        pos_--;
                    }
                    break read_loop;
                }
                if (c == CR) {
                    sep = LineSeparator.CR;
                } else if (c == LF) {
                    sep = LineSeparator.LF;
                    break read_loop;
                } else {
                    bodyLength_++;
                }
            }
        }

        if (eof_ && bodyBuffer_ == null && bodyLength_ == 0) {
            lineBody_ = null;
            lineSeparator_ = null;
            return;
        }

        if (bodyBuffer_ == null) {
            lineBody_ = new String(buffer_, bodyStartPos_, bodyLength_);
        } else {
            /*
             * fillする前にbodyBufferへ退避しているので、
             * fillでEOFだった場合はbodyBufferだけで良い。
             */
            if (eof_) {
            } else {
                bodyBuffer_.append(buffer_, bodyStartPos_, bodyLength_);
            }
            lineBody_ = bodyBuffer_.toString();
        }

        lineNumber_++;
        lineSeparator_ = sep;
    }

    protected void readIfNecessary() throws IOException {
        if (length_ <= pos_) {
            if (0 < bodyLength_) {
                if (bodyBuffer_ == null) {
                    bodyBuffer_ = new StringBuilder();
                }
                bodyBuffer_.append(buffer_, bodyStartPos_, bodyLength_);
            }
            final int len = readable_.read(charBuffer_);
            if (len == -1) {
                eof_ = true;
                pos_ = 0;
                length_ = 0;
            } else {
                charBuffer_.rewind();
                length_ = len;
                pos_ = 0;
                bodyStartPos_ = 0;
                bodyLength_ = 0;
            }
        }
    }

    public void pushback(final Line line) {
        if (pushback_ == null) {
            pushback_ = new LinkedList<Line>();
        }
        pushback_.push(line);
        lineNumber_--;
        // XXX この時点では手前の行のことは忘れているため、わからない
        lineBody_ = null;
        lineSeparator_ = null;
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

    /**
     * 初期値は"0"。
     * 1行目を読み終えたら"1"。
     */
    @Override
    public int getLineNumber() {
        return lineNumber_;
    }

    public LineSeparator getLineSeparator() {
        return lineSeparator_;
    }

    @Override
    public Iterator<Line> iterator() {
        return new LineReadableIterator(this);
    }

}
