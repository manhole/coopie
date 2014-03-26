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

public interface CsvColumnSetup {

    ColumnBuilder column(CsvColumnDef columnDef);

    ColumnBuilder column(String name);

    CompositeColumnBuilder columns(final SetupBlock<CsvCompositeColumnSetup> compositeSetup);

    public interface ColumnBuilder {

        /*
         * カラム名とプロパティ名が異なる場合は、当メソッドでプロパティ名を指定してください。
         */
        ColumnBuilder toProperty(final String propertyName);

        /*
         * 型変換およびCompositeカラムである場合は、
         * 当メソッドでconverterを指定してください。
         */
        ColumnBuilder withConverter(Converter converter);

        ColumnBuilder withColumnNameMatcher(ColumnNameMatcher columnNameMatcher);

    }

    public interface CsvCompositeColumnSetup {

        ColumnBuilder column(String name);

    }

    public interface CompositeColumnBuilder {

        CompositeColumnBuilder toProperty(final String propertyName);

        CompositeColumnBuilder withConverter(Converter converter);

    }

}
