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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomString {

    private final Random random_ = new Random();
    private final CustomRadixString rs_;

    public RandomString(final String text) {
        rs_ = new CustomRadixString(text);
    }

    public String generate(final int length) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            final int pos = random_.nextInt(rs_.getRadix());
            final String s = rs_.toString(pos);
            sb.append(s);
        }
        return sb.toString();
    }

    public static class AsciiCodeBlock {

        private boolean symbolCharacter_;
        private boolean upperCaseAlphabet_;
        private boolean lowerCaseAlphabet_;
        private boolean digit_;

        public boolean isControlCharacter() {
            return symbolCharacter_;
        }

        public void setSymbolCharacter(final boolean controlCharacter) {
            symbolCharacter_ = controlCharacter;
        }

        public boolean isUpperCaseLetter() {
            return upperCaseAlphabet_;
        }

        public void setUpperCaseAlphabet(final boolean upperCaseAlphabet) {
            upperCaseAlphabet_ = upperCaseAlphabet;
        }

        public boolean isLowerCaseLetter() {
            return lowerCaseAlphabet_;
        }

        public void setLowerCaseAlphabet(final boolean lowerCaseAlphabet) {
            lowerCaseAlphabet_ = lowerCaseAlphabet;
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
                    if (lowerCaseAlphabet_) {
                        list.add(c);
                    }
                } else if (Character.isUpperCase(i)) {
                    if (upperCaseAlphabet_) {
                        list.add(c);
                    }
                } else if (!Character.isISOControl(i) && !Character.isSpaceChar(i)) {
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

    public static class CustomRadixString {

        private final String chars_;
        private final int radix_;

        public CustomRadixString(final String chars) {
            if (Text.isEmpty(chars)) {
                throw new IllegalArgumentException(String.valueOf(chars));
            }
            chars_ = chars;
            radix_ = chars.length();
        }

        public int getRadix() {
            return radix_;
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
            return toString(BigInteger.valueOf(intValue));
        }

        public String toString(final BigInteger value) {
            BigDecimal quotient = new BigDecimal(value);
            BigDecimal remainder = null;
            final BigDecimal radix = BigDecimal.valueOf(radix_);
            final StringBuilder sb = new StringBuilder();
            while (true) {
                final BigDecimal[] ret = quotient.divideAndRemainder(radix);
                quotient = ret[0];
                remainder = ret[1];
                final int pos = remainder.intValue();
                sb.append(chars_.charAt(pos));
                if (BigDecimal.ZERO.compareTo(quotient) == 0) {
                    break;
                }
            }
            sb.reverse();
            return sb.toString();
        }

    }

}
