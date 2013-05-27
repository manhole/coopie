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

package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.ColumnNameMatcher;
import jp.sourceforge.hotchpotch.coopie.csv.Converter;
import jp.sourceforge.hotchpotch.coopie.csv.ExactNameMatcher;
import jp.sourceforge.hotchpotch.coopie.csv.PassthroughStringConverter;

class DefaultFixedLengthColumnDef implements FixedLengthColumnDef {

    private String propertyName_;
    private int beginIndex_;
    private int endIndex_;
    private Converter<?, ?> converter_ = PassthroughStringConverter
            .getInstance();
    private ColumnNameMatcher columnNameMatcher_ = ExactNameMatcher
            .getInstance();

    @Override
    public String getPropertyName() {
        return propertyName_;
    }

    @Override
    public void setPropertyName(final String propertyName) {
        propertyName_ = propertyName;
    }

    @Override
    public int getBeginIndex() {
        return beginIndex_;
    }

    public void setBeginIndex(final int beginIndex) {
        beginIndex_ = beginIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex_;
    }

    public void setEndIndex(final int endIndex) {
        endIndex_ = endIndex;
    }

    @Override
    public Converter<?, ?> getConverter() {
        return converter_;
    }

    @Override
    public void setConverter(final Converter<?, ?> converter) {
        converter_ = converter;
    }

    @Override
    public ColumnNameMatcher getColumnNameMatcher() {
        return columnNameMatcher_;
    }

    @Override
    public void setColumnNameMatcher(final ColumnNameMatcher columnNameMatcher) {
        columnNameMatcher_ = columnNameMatcher;
    }

}
