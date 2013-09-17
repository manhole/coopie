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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class RandomStringTest {

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
        assertThat(chars, is("abcdefghijklmnopqrstuvwxyz".toCharArray()));
    }

    private static class AsciiCodeBlock {

        private boolean controlCharacter_;
        private boolean upperCaseLetter_;
        private boolean lowerCaseLetter_;
        private boolean digit_;

        public boolean isControlCharacter() {
            return controlCharacter_;
        }

        public void setControlCharacter(final boolean controlCharacter) {
            controlCharacter_ = controlCharacter;
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
                if (digit_) {
                    if (Character.isDigit(i)) {
                        list.add(c);
                    }
                }
                if (lowerCaseLetter_) {
                    if (Character.isLowerCase(i)) {
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

    private static class RandomString {

    }

}
