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

public interface ElementParserContext {

    /**
     * 要素の途中であるかを返します。
     * trueの場合は、要素の途中で次行へ進んだことを意味します。(改行を含む要素)
     *
     * @return trueの場合、要素の最中であることを示します。
     * falseの場合、要素やレコードの区切り(要素の最中ではない)であることを示します。
     */
    boolean isInElement();

}
