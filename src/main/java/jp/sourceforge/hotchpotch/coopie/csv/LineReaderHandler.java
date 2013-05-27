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

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public interface LineReaderHandler {

    /**
     * 1行読むタイミングで呼ばれます。
     * 行へ手を入れたい場合は当メソッドにて変更して返却してください。
     * 
     */
    // 当メソッドが返却した値が、 {@link #readRecord(ElementReader)} へ流れていきます。
    // (当メソッドにて読み飛ばした行は、{@link #readRecord(ElementReader)} へ流れません。)
    Line readLine(LineReader lineReader, Line reusableLine) throws IOException;

    /**
     * 1行読むタイミングで呼ばれます。
     * 
     * 行を使用するかを判定します。
     * trueを返すとこの行は採用されます。
     * falseを返すとこの行はskipされます。
     * 
     */
    // 当メソッドがtrueを返した行が、 {@link #readRecord(ElementReader)} へ流れていきます。
    boolean acceptLine(Line line, ElementParserContext parserContext);

}
