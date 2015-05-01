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

package jp.sourceforge.hotchpotch.coopie.logging;

import java.util.Collections;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.internal.CollectionsUtil;

public class SimpleLog implements Log {

    private StringBuilder format_;
    private List<Object> argList_;

    public SimpleLog() {
    }

    public SimpleLog(final String format, final Object[] args) {
        append(format, args);
    }

    @Override
    public String getFormat() {
        if (format_ == null) {
            return null;
        }
        return format_.toString();
    }

    @Override
    public Object[] getArgs() {
        if (argList_ == null) {
            return null;
        }
        return argList_.toArray(new Object[argList_.size()]);
    }

    public void append(final String format, final Object... args) {
        appendFormat(format);
        appendArgs(args);
    }

    public void appendFormat(final String format) {
        if (format != null) {
            initFormatIfNeed();
            format_.append(format);
        }
    }

    private void appendArgs(final Object... args) {
        if (args != null) {
            initArgsIfNeed();
            Collections.addAll(argList_, args);
        }
    }

    private void initFormatIfNeed() {
        if (format_ == null) {
            format_ = new StringBuilder();
        }
    }

    private void initArgsIfNeed() {
        if (argList_ == null) {
            argList_ = CollectionsUtil.newArrayList();
        }
    }

}
