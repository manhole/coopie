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

public class VarArgs {

    @SafeVarargs
    public static <T> T[] a(final T... ts) {
        return ts;
    }

    public static boolean[] a(final boolean... args) {
        return args;
    }

    public static char[] a(final char... args) {
        return args;
    }

    public static byte[] a(final byte... args) {
        return args;
    }

    public static short[] a(final short... args) {
        return args;
    }

    public static int[] a(final int... args) {
        return args;
    }

    public static long[] a(final long... args) {
        return args;
    }

    public static float[] a(final float... args) {
        return args;
    }

    public static double[] a(final double... args) {
        return args;
    }

}
