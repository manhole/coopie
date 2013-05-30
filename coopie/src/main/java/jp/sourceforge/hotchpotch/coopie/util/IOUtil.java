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

public class IOUtil {

    public static final char CR = '\r';
    public static final char LF = '\n';
    public static final String CR_S = Character.toString(CR);
    public static final String LF_S = Character.toString(LF);
    public static final String CRLF = CR_S + LF_S;

    private static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    public static String getSystemLineSeparator() {
        return LINE_SEPARATOR;
    }

}
