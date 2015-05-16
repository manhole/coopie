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

/**
 * @author manhole
 */
public class CodePoints {

    private final int[] codePoints_;

    protected CodePoints(final int[] codePoints) {
        codePoints_ = codePoints;
    }

    public static CodePoints create(final CharSequence str) {
        final int len = str.length();
        // str.length 以下になるはず
        final int[] codePoints = new int[len];
        int codePointIndex = 0;
        for (int i = 0; i < len; i++) {
            final char ch = str.charAt(i);
            if (Character.isHighSurrogate(ch)) {
                final int j = i + 1;
                if (j < len) {
                    final char ch2 = str.charAt(j);
                    if (Character.isLowSurrogate(ch2)) {
                        final int cp = Character.toCodePoint(ch, ch2);
                        //final int cp = str.codePointAt(i);
                        codePoints[codePointIndex] = cp;
                        codePointIndex++;
                        i = j;
                        continue;
                    }
                }
            }

            codePoints[codePointIndex] = ch;
            codePointIndex++;
        }

        if (codePointIndex == len) {
            return new CodePoints(codePoints);
        }
        final int[] dest = new int[codePointIndex];
        System.arraycopy(codePoints, 0, dest, 0, codePointIndex);
        return new CodePoints(dest);
    }

    public int size() {
        return codePoints_.length;
    }

    public int getAt(final int index) {
        return codePoints_[index];
    }

}
