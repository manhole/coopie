package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.NumberFormat;

import org.t2framework.commons.exception.IORuntimeException;

public class FileSize {

    private static final long K = 1024;
    private static final long M = K * 1024;
    private static final long G = M * 1024;
    private static final long T = G * 1024;

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

    private static final FileSizeUnit KB = new SimpleFileSizeUnit("KB", K);
    private static final FileSizeUnit MB = new SimpleFileSizeUnit("MB", M);
    private static final FileSizeUnit GB = new SimpleFileSizeUnit("GB", G);
    private static final FileSizeUnit TB = new SimpleFileSizeUnit("TB", T);

    private static final int bufferSize = 1024 * 8;

    private final long size_;

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
        final long size = getSize();
        final FileSizeUnit unit = detectUnit(size);
        final StringBuilder sb = new StringBuilder();
        appendTo(unit, size, sb);
        return sb.toString();
    }

    @Override
    public String toString() {
        final long size = getSize();
        final FileSizeUnit unit = detectUnit(size);
        final StringBuilder sb = new StringBuilder();
        appendTo(unit, size, sb);
        if (unit == B) {
            return sb.toString();
        }

        sb.append(" (");
        appendTo(B, size, sb);
        sb.append(")");
        return sb.toString();
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

}
