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

import jp.sourceforge.hotchpotch.coopie.util.Text;

public class ElementEditors {

    public static ElementEditor passThrough() {
        return PASS_THROUGH;
    }

    public static ElementEditor trim() {
        return TRIM;
    }

    public static ElementEditor trimWhitespace() {
        return TRIM_WHITESPACE;
    }

    /**
     * 何もしません。
     */
    private static final ElementEditor PASS_THROUGH = element -> element;

    /**
     * {@link String#trim()}します。
     */
    private static final ElementEditor TRIM = element -> element.trim();

    /**
     * {@link Character#isWhitespace(char)}である文字をtrimします。
     */
    private static final ElementEditor TRIM_WHITESPACE = element -> Text.trimWhitespace(element);

}
