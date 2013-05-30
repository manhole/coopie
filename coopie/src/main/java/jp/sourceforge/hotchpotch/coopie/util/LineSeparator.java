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

public interface LineSeparator {

    /**
     * @return 改行文字
     */
    String getSeparator();

    LineSeparator CR = new LineSeparatorImpl(IOUtil.CR_S, "<CR>");

    LineSeparator LF = new LineSeparatorImpl(IOUtil.LF_S, "<LF>");

    LineSeparator CRLF = new LineSeparatorImpl(IOUtil.CRLF, "<CRLF>");

    /**
     * 改行文字で終了しない場合。(改行で終了しない最終行など)
     */
    LineSeparator NONE = new LineSeparatorImpl("", "<NONE>");

}
