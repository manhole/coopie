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

import static jp.sourceforge.hotchpotch.coopie.util.MutableBigDecimal._bd;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

public class MutableBigDecimalTest {

    @Test
    public void construct1() {
        final MutableBigDecimal v = new MutableBigDecimal();
        assertEquals(_bd("0"), v.getValue());
    }

    @Test
    public void construct2() {
        final MutableBigDecimal v = new MutableBigDecimal(_bd("12.3"));
        assertEquals(_bd("12.3"), v.getValue());
    }

    @Test
    public void valueOfOrZero_string() {
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero((String) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero("123.4");
            assertEquals(_bd("123.4"), v.getValue());
        }
    }

    @Test
    public void valueOfOrZero_integer() {
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero((Integer) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(123);
            assertEquals(_bd("123"), v.getValue());
        }
    }

    @Test
    public void valueOfOrZero_long() {
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero((Long) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(12345L);
            assertEquals(_bd("12345"), v.getValue());
        }
    }

    @Test
    public void valueOfOrZero_double() {
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero((Double) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(12345.6);
            assertEquals(_bd("12345.6"), v.getValue());
        }
    }

    @Test
    public void valueOfOrZero_bigDecimal() {
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero((BigDecimal) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(_bd("12345.6"));
            assertEquals(_bd("12345.6"), v.getValue());
        }
    }

    @Test
    public void setValue() throws Throwable {
        final MutableBigDecimal v = new MutableBigDecimal();
        assertEquals(_bd("0"), v.getValue());
        v.setValue(_bd("1123"));
        assertEquals(_bd("1123"), v.getValue());
    }

    @Test
    public void add() throws Throwable {
        final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero("123");
        v.add("3");
        assertEquals(new BigDecimal(126), v.getValue());
        v.add((String) null);
        assertEquals(new BigDecimal(126), v.getValue());

        v.add(5000);
        assertEquals(new BigDecimal(5126), v.getValue());
        v.add((Integer) null);
        assertEquals(new BigDecimal(5126), v.getValue());

        v.add(-12.3);
        assertEquals(_bd("5113.7"), v.getValue());
        v.add((Double) null);
        assertEquals(_bd("5113.7"), v.getValue());

        v.add(1234567890123L);
        assertEquals(_bd("1234567895236.7"), v.getValue());
        v.add((Long) null);
        assertEquals(_bd("1234567895236.7"), v.getValue());

        v.add(_bd("100000000000000"));
        assertEquals(_bd("101234567895236.7"), v.getValue());
        v.add((BigDecimal) null);
        assertEquals(_bd("101234567895236.7"), v.getValue());
    }

    @Test
    public void subtract() throws Throwable {
        final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero("123");
        v.subtract("3");
        assertEquals(_bd("120"), v.getValue());
        v.subtract((String) null);
        assertEquals(_bd("120"), v.getValue());

        v.subtract(5000);
        assertEquals(_bd("-4880"), v.getValue());
        v.subtract((Integer) null);
        assertEquals(_bd("-4880"), v.getValue());

        v.subtract(-12.3);
        assertEquals(_bd("-4867.7"), v.getValue());
        v.subtract((Double) null);
        assertEquals(_bd("-4867.7"), v.getValue());

        v.subtract(1234567890123L);
        assertEquals(_bd("-1234567894990.7"), v.getValue());
        v.subtract((Long) null);
        assertEquals(_bd("-1234567894990.7"), v.getValue());

        v.subtract(_bd("100000000000000"));
        assertEquals(_bd("-101234567894990.7"), v.getValue());
        v.subtract((BigDecimal) null);
        assertEquals(_bd("-101234567894990.7"), v.getValue());
    }

    @Test
    public void multiply() throws Throwable {
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero("5");
            v.multiply("70");
            assertEquals(_bd("350"), v.getValue());
            v.multiply((String) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(-120);
            v.multiply(-10);
            assertEquals(_bd("1200"), v.getValue());
            v.multiply((Integer) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(12345678901L);
            v.multiply(10L);
            assertEquals(_bd("123456789010"), v.getValue());
            v.multiply((Integer) null);
            assertEquals(_bd("0"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(12345678901.12);
            v.multiply(10.0);
            assertEquals(_bd("123456789011.200"), v.getValue());
            v.multiply((Integer) null);
            assertEquals(_bd("0.000"), v.getValue());
        }
        {
            final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(_bd("1122"));
            v.multiply(_bd("10.0"));
            assertEquals(_bd("11220.0"), v.getValue());
            v.multiply((BigDecimal) null);
            assertEquals(_bd("0.0"), v.getValue());
        }
    }

    /*
     * toString()はtoPlainString()を返すようにする。
     */
    @Test
    public void test_toString() throws Throwable {
        final BigDecimal value = new BigDecimal("1234.55").setScale(-2, BigDecimal.ROUND_HALF_UP);

        final MutableBigDecimal v = MutableBigDecimal.valueOfOrZero(value);
        assertEquals("1.2E+3", v.getValue().toString());
        assertEquals("1.2E+3", v.getValue().toEngineeringString());
        assertEquals("1200", v.getValue().toPlainString());
        assertEquals("1200", v.toString());

        v.setValue(null);
        assertEquals("null", v.toString());
    }

}
