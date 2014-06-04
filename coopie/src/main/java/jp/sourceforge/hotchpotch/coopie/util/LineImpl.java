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

public class LineImpl implements Line {

    private String body_;
    private int number_;
    private LineSeparator separator_;

    public LineImpl() {
    }

    public LineImpl(final String body, final int number, final LineSeparator separator) {
        body_ = body;
        number_ = number;
        separator_ = separator;
    }

    @Override
    public String getBody() {
        return body_;
    }

    public void setBody(final String body) {
        body_ = body;
    }

    @Override
    public String getBodyAndSeparator() {
        return body_ + separator_.getSeparator();
    }

    @Override
    public int getNumber() {
        return number_;
    }

    public void setNumber(final int number) {
        number_ = number;
    }

    @Override
    public LineSeparator getSeparator() {
        return separator_;
    }

    public void setSeparator(final LineSeparator separator) {
        separator_ = separator;
    }

    @Override
    public Line reinit(final String body, final int number, final LineSeparator separator) {
        setBody(body);
        setNumber(number);
        setSeparator(separator);
        return this;
    }

    @Override
    public Line createCopy() {
        final Line copy = new LineImpl(body_, number_, separator_);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("[%s]%s%s", number_, body_, separator_);
    }

}
