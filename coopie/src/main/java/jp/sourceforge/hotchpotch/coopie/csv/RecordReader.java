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

import jp.sourceforge.hotchpotch.coopie.util.Closable;

public interface RecordReader<BEAN> extends Closable {

    BEAN read();

    void read(BEAN bean);

    boolean hasNext();

    /**
     * レコード番号を返します。
     * 初期値は0です。
     * {@link #read()}で1件目を取得した後は、1を返すようになります。
     * 同様に、10件目を取得した後は10を返します。
     * 最後まで読むと、それ以上大きい値を返さないようになります。
     */
    int getRecordNumber();

}
