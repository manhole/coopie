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

public class MutableBigDecimal {

    private BigDecimal value_;

    public MutableBigDecimal() {
        value_ = BigDecimal.ZERO;
    }

    public MutableBigDecimal(final BigDecimal value) {
        value_ = value;
    }

    public BigDecimal getValue() {
        return value_;
    }

    public void setValue(final BigDecimal value) {
        value_ = value;
    }

    public void add(final String augend) {
        final BigDecimal v = _bdOrZero(augend);
        _add(v);
    }

    public void add(final Integer augend) {
        final BigDecimal v = _bdOrZero(augend);
        _add(v);
    }

    public void add(final Long augend) {
        final BigDecimal v = _bdOrZero(augend);
        _add(v);
    }

    public void add(final Double augend) {
        final BigDecimal v = _bdOrZero(augend);
        _add(v);
    }

    public void add(final BigDecimal augend) {
        final BigDecimal v = _bdOrZero(augend);
        _add(v);
    }

    private void _add(final BigDecimal augend) {
        value_ = value_.add(augend);
    }

    public void multiply(final String multiplicand) {
        final BigDecimal v = _bdOrZero(multiplicand);
        _multiply(v);
    }

    public void multiply(final Integer multiplicand) {
        final BigDecimal v = _bdOrZero(multiplicand);
        _multiply(v);
    }

    public void multiply(final Long multiplicand) {
        final BigDecimal v = _bdOrZero(multiplicand);
        _multiply(v);
    }

    public void multiply(final Double multiplicand) {
        final BigDecimal v = _bdOrZero(multiplicand);
        _multiply(v);
    }

    public void multiply(final BigDecimal multiplicand) {
        final BigDecimal v = _bdOrZero(multiplicand);
        _multiply(v);
    }

    private void _multiply(final BigDecimal multiplicand) {
        value_ = value_.multiply(multiplicand);
    }

    public void subtract(final String subtrahend) {
        final BigDecimal v = _bdOrZero(subtrahend);
        _subtract(v);
    }

    public void subtract(final Integer subtrahend) {
        final BigDecimal v = _bdOrZero(subtrahend);
        _subtract(v);
    }

    public void subtract(final Long subtrahend) {
        final BigDecimal v = _bdOrZero(subtrahend);
        _subtract(v);
    }

    public void subtract(final Double subtrahend) {
        final BigDecimal v = _bdOrZero(subtrahend);
        _subtract(v);
    }

    public void subtract(final BigDecimal subtrahend) {
        final BigDecimal v = _bdOrZero(subtrahend);
        _subtract(v);
    }

    private void _subtract(final BigDecimal subtrahend) {
        value_ = value_.subtract(subtrahend);
    }

    public static MutableBigDecimal valueOfOrZero(final String v) {
        return _new(_bdOrZero(v));
    }

    public static MutableBigDecimal valueOfOrZero(final Integer v) {
        return _new(_bdOrZero(v));
    }

    public static MutableBigDecimal valueOfOrZero(final Long v) {
        return _new(_bdOrZero(v));
    }

    public static MutableBigDecimal valueOfOrZero(final Double v) {
        return _new(_bdOrZero(v));
    }

    public static MutableBigDecimal valueOfOrZero(final BigDecimal v) {
        return _new(_bdOrZero(v));
    }

    private static MutableBigDecimal _new(final BigDecimal value) {
        return new MutableBigDecimal(value);
    }

    static BigDecimal _bd(final String v) {
        return new BigDecimal(v);
    }

    private static BigDecimal _bd(final Integer v) {
        return BigDecimal.valueOf(v.longValue());
    }

    private static BigDecimal _bd(final Long v) {
        return BigDecimal.valueOf(v.longValue());
    }

    private static BigDecimal _bd(final Double v) {
        return BigDecimal.valueOf(v.doubleValue());
    }

    static BigDecimal _bdOrZero(final String v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return _bd(v);
    }

    private static BigDecimal _bdOrZero(final Integer v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return _bd(v);
    }

    private static BigDecimal _bdOrZero(final Long v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return _bd(v);
    }

    private static BigDecimal _bdOrZero(final Double v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return _bd(v);
    }

    private static BigDecimal _bdOrZero(final BigDecimal v) {
        if (v == null) {
            return BigDecimal.ZERO;
        }
        return v;
    }

    @Override
    public String toString() {
        if (value_ == null) {
            return String.valueOf(value_);
        }
        return value_.toPlainString();
    }

}
