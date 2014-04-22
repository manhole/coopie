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

import java.util.List;

public interface FixedLengthRecordDef {

    List<? extends FixedLengthColumnDef> getColumnDefs();

    void addColumnDef(FixedLengthColumnDef columnDef);

    List<? extends FixedLengthColumnsDef> getColumnsDefs();

    void addColumnsDef(FixedLengthColumnsDef columnsDef);

    /*
     * Compositeも含む
     */
    List<? extends FixedLengthColumnDef> getAllColumnDefs();

}
