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

package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

public class SimpleColumnName implements ColumnName {

    /**
     * CSVの項目名
     */
    private String label_;

    private ColumnNameMatcher columnNameMatcher_ = ExactNameMatcher.getInstance();

    public SimpleColumnName() {
    }

    public SimpleColumnName(final String label) {
        setLabel(label);
    }

    @Override
    public String getLabel() {
        return label_;
    }

    public void setLabel(final String label) {
        label_ = label;
    }

    @Override
    public boolean labelEquals(final String label) {
        return columnNameMatcher_.matches(this, label);
    }

    public void setColumnNameMatcher(final ColumnNameMatcher columnNameMatcher) {
        columnNameMatcher_ = columnNameMatcher;
    }

    @Override
    public String toString() {
        return new ToStringFormat().format(this);
    }

}
