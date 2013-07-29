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

public class LineSeparatorImpl implements LineSeparator {

    private final String str_;
    private final String label_;

    public LineSeparatorImpl(final String str, final String label) {
        str_ = str;
        label_ = label;
    }

    @Override
    public String getSeparator() {
        return str_;
    }

    @Override
    public int hashCode() {
        return str_.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LineSeparatorImpl)) {
            return false;
        }
        final LineSeparatorImpl another = (LineSeparatorImpl) obj;
        return str_.equals(another.str_);
    }

    @Override
    public String toString() {
        return label_;
    }

}
