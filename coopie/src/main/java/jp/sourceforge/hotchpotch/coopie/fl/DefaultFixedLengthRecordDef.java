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

import org.t2framework.commons.util.CollectionsUtil;

class DefaultFixedLengthRecordDef implements FixedLengthRecordDef {

    final List<FixedLengthColumnDef> columnDefs_ = CollectionsUtil.newArrayList();
    private final List<FixedLengthColumnsDef> columnsDefs_ = CollectionsUtil.newArrayList();

    public boolean isEmpty() {
        return getColumnDefs().isEmpty();
    }

    @Override
    public List<? extends FixedLengthColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    @Override
    public void addColumnDef(final FixedLengthColumnDef columnDef) {
        columnDefs_.add(columnDef);
    }

    @Override
    public List<? extends FixedLengthColumnsDef> getColumnsDefs() {
        return columnsDefs_;
    }

    @Override
    public void addColumnsDef(final FixedLengthColumnsDef columnsDef) {
        columnsDefs_.add(columnsDef);
    }

    @Override
    public List<? extends FixedLengthColumnDef> getAllColumnDefs() {
        final List<FixedLengthColumnDef> all = CollectionsUtil.newArrayList();
        all.addAll(getColumnDefs());
        final List<? extends FixedLengthColumnsDef> columnsDefs = getColumnsDefs();
        for (final FixedLengthColumnsDef columnsDef : columnsDefs) {
            final List<FixedLengthColumnDef> columnDefs = columnsDef.getColumnDefs();
            all.addAll(columnDefs);
        }
        return all;
    }

}
