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

import java.math.RoundingMode;
import java.text.NumberFormat;

public class ByteSizeUnits {

    public static final ByteSizeUnit B = new SimpleByteSizeUnit("B", 0) {

        @Override
        protected double convert(final long value) {
            return value;
        };

        @Override
        protected void initialize() {
            final NumberFormat format = getNumberFormat();
            format.setRoundingMode(RoundingMode.HALF_UP);
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(0);
        }

    };

    /**
     * Kilobyte
     */
    public static final ByteSizeUnit KB = DecimalUnits.KB;
    /**
     * Megabyte
     */
    public static final ByteSizeUnit MB = DecimalUnits.MB;
    /**
     * Gigabyte
     */
    public static final ByteSizeUnit GB = DecimalUnits.GB;
    /**
     * Terabyte
     */
    public static final ByteSizeUnit TB = DecimalUnits.TB;
    /**
     * Petabyte
     */
    public static final ByteSizeUnit PB = DecimalUnits.PB;

    /**
     * kilobinary
     */
    public static final ByteSizeUnit KiB = BinaryUnits.KB;
    /**
     * megabinary
     */
    public static final ByteSizeUnit MiB = BinaryUnits.MB;
    /**
     * gigabinary
     */
    public static final ByteSizeUnit GiB = BinaryUnits.GB;
    /**
     * terabinary
     */
    public static final ByteSizeUnit TiB = BinaryUnits.TB;
    /**
     * petabinary
     */
    public static final ByteSizeUnit PiB = BinaryUnits.PB;

    private static class Coefficient {

        private final int base_;

        public Coefficient(final int base) {
            base_ = base;
        }

        public long pow(final int exponent) {
            final double pow = Math.pow(base_, exponent);
            return (long) pow;
        }

    }

    static interface UnitsTable {

        ByteSizeUnit detectUnit(long size);

    }

    /*
     * Binary prefix
     * IEC
     * http://en.wikipedia.org/wiki/Binary_prefix
     */
    static class BinaryUnits implements UnitsTable {

        private static final Coefficient COEFFICIENT = new Coefficient(1024);
        private static final BinaryUnits INSTANCE = new BinaryUnits();

        public static BinaryUnits getInstance() {
            return INSTANCE;
        }

        // kilobinary
        private static final ByteSizeUnit KB = new SimpleByteSizeUnit("KiB",
                COEFFICIENT.pow(1));
        // megabinary
        private static final ByteSizeUnit MB = new SimpleByteSizeUnit("MiB",
                COEFFICIENT.pow(2));
        // gigabinary
        private static final ByteSizeUnit GB = new SimpleByteSizeUnit("GiB",
                COEFFICIENT.pow(3));
        // terabinary
        private static final ByteSizeUnit TB = new SimpleByteSizeUnit("TiB",
                COEFFICIENT.pow(4));
        private static final ByteSizeUnit PB = new SimpleByteSizeUnit("PiB",
                COEFFICIENT.pow(5));

        @Override
        public ByteSizeUnit detectUnit(final long size) {
            if (KB.lessThan(size)) {
                return B;
            } else if (MB.lessThan(size)) {
                return KB;
            } else if (GB.lessThan(size)) {
                return MB;
            } else if (TB.lessThan(size)) {
                return GB;
            } else if (PB.lessThan(size)) {
                return TB;
            }
            return PB;
        }

    }

    /*
     * SI prefix system
     * (International System of Units (SI))
     */
    static class DecimalUnits implements UnitsTable {

        private static final Coefficient COEFFICIENT = new Coefficient(1000);
        private static final DecimalUnits INSTANCE = new DecimalUnits();

        public static DecimalUnits getInstance() {
            return INSTANCE;
        }

        // Kilobyte
        private static final ByteSizeUnit KB = new SimpleByteSizeUnit("kB",
                COEFFICIENT.pow(1));
        // Megabyte
        private static final ByteSizeUnit MB = new SimpleByteSizeUnit("MB",
                COEFFICIENT.pow(2));
        // Gigabyte
        private static final ByteSizeUnit GB = new SimpleByteSizeUnit("GB",
                COEFFICIENT.pow(3));
        // Terabyte
        private static final ByteSizeUnit TB = new SimpleByteSizeUnit("TB",
                COEFFICIENT.pow(4));
        // Petabyte
        private static final ByteSizeUnit PB = new SimpleByteSizeUnit("PB",
                COEFFICIENT.pow(5));

        @Override
        public ByteSizeUnit detectUnit(final long size) {
            if (KB.lessThan(size)) {
                return B;
            } else if (MB.lessThan(size)) {
                return KB;
            } else if (GB.lessThan(size)) {
                return MB;
            } else if (TB.lessThan(size)) {
                return GB;
            } else if (PB.lessThan(size)) {
                return TB;
            }
            return PB;
        }

    }

    static class SimpleByteSizeUnit implements ByteSizeUnit {

        private final String label_;
        private final long coefficient_;
        private final NumberFormat format_;

        SimpleByteSizeUnit(final String label, final long coefficient) {
            label_ = label;
            coefficient_ = coefficient;
            format_ = NumberFormat.getNumberInstance();
            initialize();
        }

        protected void initialize() {
            format_.setRoundingMode(RoundingMode.HALF_UP);
            format_.setMinimumFractionDigits(2);
            format_.setMaximumFractionDigits(2);
        }

        @Override
        public String getUnitLabel() {
            return label_;
        }

        protected double convert(final long value) {
            return value / (double) coefficient_;
        }

        @Override
        public boolean lessThan(final long value) {
            return value < coefficient_;
        }

        @Override
        public String format(final long value) {
            final NumberFormat format = getNumberFormat();
            final double converted = convert(value);
            final String s = format.format(converted);
            return s;
        }

        @Override
        public ByteSize multiply(final int size) {
            return ByteSize.create(coefficient_ * size);
        }

        protected NumberFormat getNumberFormat() {
            return format_;
        }

    }

}
