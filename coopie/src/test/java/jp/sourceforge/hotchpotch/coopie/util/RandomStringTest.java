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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RandomStringTest {

    private final String lowerChars = "abcdefghijklmnopqrstuvwxyz";
    private final String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String symbol1 = "!\"#$%&'()*+,-./";
    private final String symbol2 = ":;<=>?@";
    private final String symbol3 = "[\\]^_`";
    private final String symbol4 = "{|}~";

    @Test
    public void digit1() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setDigit(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars, is("0123456789".toCharArray()));
    }

    @Test
    public void letter1() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setLowerCaseLetter(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars, is(lowerChars.toCharArray()));
    }

    @Test
    public void letter2() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setUpperCaseLetter(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars, is(upperChars.toCharArray()));
    }

    @Test
    public void symbol1() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setSymbolCharacter(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars,
                is((symbol1 + symbol2 + symbol3 + symbol4).toCharArray()));
    }

    @Test
    public void lowerAndUpper() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setLowerCaseLetter(true);
        codes.setUpperCaseLetter(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars, is((upperChars + lowerChars).toCharArray()));
    }

    @Test
    public void radix1() throws Throwable {
        // ## Arrange ##
        // 10進数
        final CustomRadixString r = new CustomRadixString("0123456789");

        // ## Act ##
        // ## Assert ##
        assertThat(r.toString(0), is("0"));
        assertThat(r.toString(5), is("5"));
        assertThat(r.toString(10), is("10"));
        assertThat(r.toString(2003), is("2003"));
    }

    @Test
    public void radix2() throws Throwable {
        // ## Arrange ##
        // 10進数
        final CustomRadixString r = new CustomRadixString("01234567");

        // ## Act ##
        // ## Assert ##
        assertThat(r.toString(0), is("0"));
        assertThat(r.toString(5), is("5"));
        assertThat(r.toString(8), is("10"));
        assertThat(r.toString(10), is("12"));
        assertThat(r.toString(2003), is("3723"));
    }

    private static class AsciiCodeBlock {

        private boolean symbolCharacter_;
        private boolean upperCaseLetter_;
        private boolean lowerCaseLetter_;
        private boolean digit_;

        public boolean isControlCharacter() {
            return symbolCharacter_;
        }

        public void setSymbolCharacter(final boolean controlCharacter) {
            symbolCharacter_ = controlCharacter;
        }

        public boolean isUpperCaseLetter() {
            return upperCaseLetter_;
        }

        public void setUpperCaseLetter(final boolean upperCaseLetter) {
            upperCaseLetter_ = upperCaseLetter;
        }

        public boolean isLowerCaseLetter() {
            return lowerCaseLetter_;
        }

        public void setLowerCaseLetter(final boolean lowerCaseLetter) {
            lowerCaseLetter_ = lowerCaseLetter;
        }

        public boolean isDigit() {
            return digit_;
        }

        public void setDigit(final boolean digit) {
            digit_ = digit;
        }

        public char[] toCharArray() {
            final List<Character> list = new ArrayList<Character>();
            for (int i = 0; i <= 127; i++) {
                final char c = (char) i;
                if (Character.isDigit(i)) {
                    if (digit_) {
                        list.add(c);
                    }
                } else if (Character.isLowerCase(i)) {
                    if (lowerCaseLetter_) {
                        list.add(c);
                    }
                } else if (Character.isUpperCase(i)) {
                    if (upperCaseLetter_) {
                        list.add(c);
                    }
                } else if (!Character.isISOControl(i)
                        && !Character.isSpaceChar(i)) {
                    if (symbolCharacter_) {
                        list.add(c);
                    }
                }
            }

            return _toChars(list);
        }

        private char[] _toChars(final List<Character> list) {
            final char[] chars = new char[list.size()];
            int i = 0;
            for (final Character c : list) {
                chars[i] = c;
                i++;
            }
            return chars;
        }
    }

    private static class CustomRadixString {

        private final String chars_;
        private final int radix_;

        public CustomRadixString(final String chars) {
            chars_ = chars;
            radix_ = chars.length();
        }

        /*
         * 剰余を連結したものが結果となる。
         * 例: 2003
         * 2003を10で割る ... 商:200, 剰余:3
         * 200を10で割る ... 商:20, 剰余:0
         * 20を10で割る ... 商:2, 剰余:0
         * 2を10で割る ... 商:0, 剰余:2
         */
        public String toString(final int intValue) {
            BigDecimal quotient = BigDecimal.valueOf(intValue);
            BigDecimal remainder = null;
            final BigDecimal radix = BigDecimal.valueOf(radix_);
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final BigDecimal[] ret = quotient.divideAndRemainder(radix);
                quotient = ret[0];
                remainder = ret[1];
                sb.append(remainder.toPlainString());
                if (BigDecimal.ZERO.compareTo(quotient) == 0) {
                    break;
                }
            }
            sb.reverse();
            return sb.toString();
        }
    }

    private static class RandomString {

    }

}
