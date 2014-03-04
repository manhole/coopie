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

package jp.sourceforge.hotchpotch.coopie.util;

public interface Line {

    /**
     * @return 行の文字列。改行文字は含みません。
     */
    String getBody();

    /**
     * @return 行の文字列。改行文字も含みます。
     */
    String getBodyAndSeparator();

    /**
     * @return 行番号(1行目は0)
     */
    int getNumber();

    /**
     * @return 行の改行文字。
     */
    LineSeparator getSeparator();

    Line reinit(String body, int number, LineSeparator separator);

    Line createCopy();

}
