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
import java.util.NoSuchElementException;

import org.t2framework.commons.exception.IORuntimeException;

public class LineReader implements LineReadable {

    private static final char CR = IOUtil.CR;
    private static final char LF = IOUtil.LF;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 8;

    private boolean closed_;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);
    private final Readable readable_;
    private LineSeparator lineSeparator_;
    private String line_;
    private int lineNumber_;
    private Deque<Line> pushback_;
    private boolean eof_;
    private final char[] buffer_;
    private int pos_;
    private int length_;
    private final CharBuffer charBuffer_;

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
        return line_;
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
        if (line_ == null) {
            return null;
        }

        reusableLine.reinit(line_, lineNumber_, lineSeparator_);
        return reusableLine;
    }

    protected Line createLine() {
        return new LineImpl();
    }

    private void read0() throws IOException {
        if (pushback_ != null) {
            final Line line = pushback_.pop();
            line_ = line.getBody();
            lineNumber_++;
            lineSeparator_ = line.getSeparator();
            if (pushback_.isEmpty()) {
                pushback_ = null;
            }
            return;
        }

        if (eof_) {
            line_ = null;
            lineSeparator_ = null;
            return;
        }

        StringBuilder bodyBuff = null;
        int bodyStartPos = pos_;
        int bodyLength = 0;
        LineSeparator sep = LineSeparator.NONE;
        read_loop: while (true) {
            if (length_ <= pos_) {
                if (0 < bodyLength) {
                    if (bodyBuff == null) {
                        bodyBuff = new StringBuilder();
                    }
                    bodyBuff.append(buffer_, bodyStartPos, bodyLength);
                }
                fill();
                if (eof_) {
                    break read_loop;
                }
                bodyStartPos = pos_;
                bodyLength = 0;
            }

            for (; pos_ < length_;) {
                final char c = buffer_[pos_];
                pos_++;
                if (c == CR) {
                    if (length_ <= pos_) {
                        if (0 < bodyLength) {
                            if (bodyBuff == null) {
                                bodyBuff = new StringBuilder();
                            }
                            bodyBuff.append(buffer_, bodyStartPos, bodyLength);
                        }
                        fill();
                        if (eof_) {
                            sep = LineSeparator.CR;
                            break read_loop;
                        }
                        bodyStartPos = pos_;
                        bodyLength = 0;
                    }
                    if (buffer_[pos_] == LF) {
                        sep = LineSeparator.CRLF;
                        pos_++;
                    } else {
                        sep = LineSeparator.CR;
                    }
                    break read_loop;
                } else if (c == LF) {
                    sep = LineSeparator.LF;
                    break read_loop;
                } else {
                    bodyLength++;
                }
            }
        }

        if (eof_ && bodyBuff == null && bodyLength == 0) {
            line_ = null;
            lineSeparator_ = null;
            return;
        }

        if (bodyBuff == null) {
            line_ = new String(buffer_, bodyStartPos, bodyLength);
        } else {
            /*
             * fillする前にbodyBuffへ退避しているので、
             * fillでEOFだった場合はbodyBuffだけで良い。
             */
            if (eof_) {
            } else {
                bodyBuff.append(buffer_, bodyStartPos, bodyLength);
            }
            line_ = bodyBuff.toString();
        }

        lineNumber_++;
        lineSeparator_ = sep;
    }

    protected void fill() throws IOException {
        final int len = readable_.read(charBuffer_);
        if (len == -1) {
            eof_ = true;
        } else {
            charBuffer_.rewind();
            pos_ = 0;
            length_ = len;
        }
    }

    public void pushback(final Line line) {
        if (pushback_ == null) {
            pushback_ = new LinkedList<Line>();
        }
        pushback_.push(line);
        lineNumber_--;
        // XXX この時点では手前の行のことは忘れているため、わからない
        line_ = null;
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

    public Iterator<Line> iterator() {
        return new LineIterator(this);
    }

    static class LineIterator implements Iterator<Line> {

        private final LineReadable reader_;
        private Line next;

        LineIterator(final LineReadable reader) {
            reader_ = reader;
        }

        @Override
        public boolean hasNext() {
            if (next == null) {
                next = readLine();
            }
            if (next != null) {
                return true;
            }
            return false;
        }

        @Override
        public Line next() {
            if (next != null) {
                final Line t = next;
                next = null;
                return t;
            }
            final Line t = readLine();
            if (t == null) {
                throw new NoSuchElementException();
            }
            return t;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        private Line readLine() {
            try {
                return reader_.readLine();
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

    }

}
