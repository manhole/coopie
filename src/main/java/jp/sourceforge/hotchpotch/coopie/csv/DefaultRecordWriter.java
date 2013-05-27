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

public class DefaultRecordWriter<BEAN> extends AbstractRecordWriter<BEAN> {

    private ElementInOut elementInOut_;

    public DefaultRecordWriter(final RecordDesc<BEAN> recordDesc) {
        super(recordDesc);
    }

    public void setElementInOut(final ElementInOut elementInOut) {
        elementInOut_ = elementInOut;
    }

    public void open(final Appendable appendable) {
        final ElementWriter elementWriter = elementInOut_
                .openWriter(appendable);
        setElementWriter(elementWriter);
        setClosed(false);
    }

}
