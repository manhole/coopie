package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.t2framework.commons.exception.IORuntimeException;

public class FileSize {

    private static final FileSizeUnit B = new SimpleFileSizeUnit("B", 0) {

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

    public static final ToStringMode DETAIL = new DetailMode();
    public static final ToStringMode HUMAN_READABLE = new HumanReadableMode();
    public static final ToStringMode BYTE = new ByteMode();

    private static final int bufferSize = 1024 * 8;

    private final long size_;
    private ToStringMode toStringMode_ = DETAIL;
    private UnitsTable unitsTable_ = BaseType.BINARY.getUnitsTable();

    public FileSize(final long size) {
        size_ = size;
    }

    public static FileSize create(final InputStream is) {
        final byte[] buf = new byte[bufferSize];
        long l = 0;
        try {
            while (true) {
                final int len = is.read(buf);
                if (-1 == len) {
                    break;
                }
                l += len;
            }
            return new FileSize(l);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            CloseableUtil.closeNoException(is);
        }
    }

    public long getSize() {
        return size_;
    }

    public String toHumanReadableString() {
        return HUMAN_READABLE.toString(this);
    }

    @Override
    public String toString() {
        return toStringMode_.toString(this);
    }

    private void appendTo(final FileSizeUnit unit, final long size,
            final StringBuilder sb) {
        sb.append(unit.format(size));
        if (unit == B) {
            return;
        }
        sb.append(" ");
        sb.append(unit.getUnitLabel());
    }

    private FileSizeUnit detectUnit(final long size) {
        final FileSizeUnit unit = unitsTable_.detectUnit(size);
        return unit;
    }

    public void setToStringMode(final ToStringMode toStringMode) {
        if (toStringMode == null) {
            throw new NullPointerException("toStringMode");
        }
        toStringMode_ = toStringMode;
    }

    public void setBaseType(final BaseType baseType) {
        unitsTable_ = baseType.getUnitsTable();
    }

    public static enum BaseType {

        BINARY(BinaryUnits.getInstance()),

        DECIMAL(DecimalUnits.getInstance())

        ;

        private final UnitsTable unitsTable_;

        private BaseType(final UnitsTable unitsTable) {
            unitsTable_ = unitsTable;
        }

        public UnitsTable getUnitsTable() {
            return unitsTable_;
        }

    }

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

    private static interface UnitsTable {

        FileSizeUnit detectUnit(long size);

    }

    /*
     * Binary prefix
     * IEC
     * http://en.wikipedia.org/wiki/Binary_prefix
     */
    private static class BinaryUnits implements UnitsTable {

        private static final Coefficient COEFFICIENT = new Coefficient(1024);
        private static final BinaryUnits INSTANCE = new BinaryUnits();

        public static BinaryUnits getInstance() {
            return INSTANCE;
        }

        // kilobinary
        private static final FileSizeUnit KB = new SimpleFileSizeUnit("KiB",
                COEFFICIENT.pow(1));
        // megabinary
        private static final FileSizeUnit MB = new SimpleFileSizeUnit("MiB",
                COEFFICIENT.pow(2));
        // gigabinary
        private static final FileSizeUnit GB = new SimpleFileSizeUnit("GiB",
                COEFFICIENT.pow(3));
        // terabinary
        private static final FileSizeUnit TB = new SimpleFileSizeUnit("TiB",
                COEFFICIENT.pow(4));

        @Override
        public FileSizeUnit detectUnit(final long size) {
            if (KB.lessThan(size)) {
                return B;
            } else if (MB.lessThan(size)) {
                return KB;
            } else if (GB.lessThan(size)) {
                return MB;
            } else if (TB.lessThan(size)) {
                return GB;
            }
            return TB;
        }

    }

    /*
     * SI prefix system
     * (International System of Units (SI))
     */
    private static class DecimalUnits implements UnitsTable {

        private static final Coefficient COEFFICIENT = new Coefficient(1000);
        private static final DecimalUnits INSTANCE = new DecimalUnits();

        public static DecimalUnits getInstance() {
            return INSTANCE;
        }

        // Kilobyte
        private static final FileSizeUnit KB = new SimpleFileSizeUnit("kB",
                COEFFICIENT.pow(1));
        // Megabyte
        private static final FileSizeUnit MB = new SimpleFileSizeUnit("MB",
                COEFFICIENT.pow(2));
        // Gigabyte
        private static final FileSizeUnit GB = new SimpleFileSizeUnit("GB",
                COEFFICIENT.pow(3));
        // Terabyte
        private static final FileSizeUnit TB = new SimpleFileSizeUnit("TB",
                COEFFICIENT.pow(4));

        @Override
        public FileSizeUnit detectUnit(final long size) {
            if (KB.lessThan(size)) {
                return B;
            } else if (MB.lessThan(size)) {
                return KB;
            } else if (GB.lessThan(size)) {
                return MB;
            } else if (TB.lessThan(size)) {
                return GB;
            }
            return TB;
        }

    }

    public interface FileSizeUnit {

        String format(long value);

        String getUnitLabel();

        boolean lessThan(long value);

    }

    private static class SimpleFileSizeUnit implements FileSizeUnit {

        private final String label_;
        private final long coefficient_;
        private final NumberFormat format_;

        SimpleFileSizeUnit(final String label, final long coefficient) {
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

        protected NumberFormat getNumberFormat() {
            return format_;
        }

    }

    public static interface ToStringMode {

        String toString(FileSize fileSize);

    }

    static class DetailMode implements ToStringMode {

        @Override
        public String toString(final FileSize fileSize) {
            final long size = fileSize.getSize();
            final FileSizeUnit unit = fileSize.detectUnit(size);
            final StringBuilder sb = new StringBuilder();
            fileSize.appendTo(unit, size, sb);
            if (unit == B) {
                return sb.toString();
            }

            sb.append(" (");
            fileSize.appendTo(B, size, sb);
            sb.append(")");
            return sb.toString();
        }

    }

    static class HumanReadableMode implements ToStringMode {

        @Override
        public String toString(final FileSize fileSize) {
            final long size = fileSize.getSize();
            final FileSizeUnit unit = fileSize.detectUnit(size);
            final StringBuilder sb = new StringBuilder();
            fileSize.appendTo(unit, size, sb);
            return sb.toString();
        }

    }

    static class ByteMode implements ToStringMode {

        @Override
        public String toString(final FileSize fileSize) {
            final long size = fileSize.getSize();
            final StringBuilder sb = new StringBuilder();
            fileSize.appendTo(B, size, sb);
            return sb.toString();
        }

    }

}
