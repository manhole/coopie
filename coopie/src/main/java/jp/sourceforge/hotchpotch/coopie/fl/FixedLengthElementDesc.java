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

public interface FixedLengthElementDesc {

    /**
     * 1行の文字列から、当カラムぶんのデータを読みます。
     */
    String read(CharSequence line);

    /**
     * 1カラムぶんのデータから、固定長ファイルへ記述するだけの文字数とし、出力します。
     */
    void write(CharSequence elem, FixedLengthLineBuilder lineBuilder);

}
