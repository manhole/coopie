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

import jp.sourceforge.hotchpotch.coopie.csv.CsvColumnSetup;
import jp.sourceforge.hotchpotch.coopie.csv.SetupBlock;

public interface FixedLengthColumnSetup {

    CsvColumnSetup.ColumnBuilder column(FixedLengthColumnDef columnDef);

    CsvColumnSetup.ColumnBuilder column(String name, int beginIndex, int endIndex);

    CsvColumnSetup.CompositeColumnBuilder columns(final SetupBlock<FixedLengthCompositeColumnSetup> compositeSetup);

    // 複数カラムをプロパティと対応づける際に使用する
    FixedLengthColumnDef c(String name, int beginIndex, int endIndex);

    public interface FixedLengthCompositeColumnSetup {

        CsvColumnSetup.ColumnBuilder column(FixedLengthColumnDef columnDef);

        CsvColumnSetup.ColumnBuilder column(String name, int beginIndex, int endIndex);

    }

}
