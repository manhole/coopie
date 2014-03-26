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
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigInteger;

import jp.sourceforge.hotchpotch.coopie.util.RandomString.AsciiCodeBlock;
import jp.sourceforge.hotchpotch.coopie.util.RandomString.CustomRadixString;

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
        codes.setLowerCaseAlphabet(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars, is(lowerChars.toCharArray()));
    }

    @Test
    public void letter2() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setUpperCaseAlphabet(true);
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
        assertThat(chars, is((symbol1 + symbol2 + symbol3 + symbol4).toCharArray()));
    }

    @Test
    public void lowerAndUpper() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();

        // ## Act ##
        codes.setLowerCaseAlphabet(true);
        codes.setUpperCaseAlphabet(true);
        final char[] chars = codes.toCharArray();

        // ## Assert ##
        assertThat(chars, is((upperChars + lowerChars).toCharArray()));
    }

    @Test
    public void radix_empty() throws Throwable {
        try {
            new CustomRadixString("");
            fail();
        } catch (final IllegalArgumentException e) {
        }
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
        assertThat(r.toString(new BigInteger("9876543210987654321098765")), is("9876543210987654321098765"));
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

    /*
     * 数字が1つ大きい方向にずれている10進数
     */
    @Test
    public void radix3() throws Throwable {
        // ## Arrange ##
        // 10進数
        final CustomRadixString r = new CustomRadixString("1234567890");

        // ## Act ##
        // ## Assert ##
        assertThat(r.toString(0), is("1"));
        assertThat(r.toString(5), is("6"));
        assertThat(r.toString(9), is("0"));
        assertThat(r.toString(10), is("21"));
        assertThat(r.toString(2003), is("3114"));
    }

    @Test
    public void random_number() throws Throwable {
        // ## Arrange ##
        final AsciiCodeBlock codes = new AsciiCodeBlock();
        codes.setDigit(true);
        final RandomString rs = new RandomString(String.valueOf(codes.toCharArray()));

        // ## Act ##
        // ## Assert ##
        final String s1 = rs.generate(15);
        final String s2 = rs.generate(15);
        //System.out.println(s1);
        //System.out.println(s2);

        assertThat(s1.length(), is(15));
        assertThat(s2.length(), is(15));
        assertThat(s1, is(not(s2)));
    }

}
