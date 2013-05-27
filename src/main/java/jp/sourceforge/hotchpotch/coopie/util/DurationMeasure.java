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

public class DurationMeasure implements Duration {

    private long begin_ = -1;
    private long end_ = -1;

    @Override
    public long getBegin() {
        return begin_;
    }

    @Override
    public long getEnd() {
        return end_;
    }

    @Override
    public long getElapsed() {
        if (-1 == begin_) {
            throw new IllegalStateException();
        }
        if (-1 != end_) {
            return end_ - begin_;
        }
        final long l = now() - begin_;
        return l;
    }

    public void begin() {
        if (-1 != begin_) {
            throw new IllegalStateException();
        }
        begin_ = now();
    }

    public void end() {
        if (-1 == begin_) {
            throw new IllegalStateException();
        }
        if (-1 != end_) {
            throw new IllegalStateException();
        }
        end_ = now();
    }

    protected long now() {
        return System.currentTimeMillis();
    }

}
