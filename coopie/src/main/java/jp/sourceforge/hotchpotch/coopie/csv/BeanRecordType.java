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

import org.t2framework.commons.meta.BeanDesc;

public class BeanRecordType<BEAN> implements RecordType<BEAN> {

    private final BeanDesc<BEAN> beanDesc_;

    public BeanRecordType(final BeanDesc<BEAN> beanDesc) {
        beanDesc_ = beanDesc;
    }

    @Override
    public BEAN newInstance() {
        return beanDesc_.newInstance();
    }

}
