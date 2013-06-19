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

import java.io.IOException;
import java.io.InputStream;

import org.t2framework.commons.exception.IORuntimeException;

public class ByteSize {

    public static final ToStringMode DETAIL = new DetailMode();
    public static final ToStringMode HUMAN_READABLE = new HumanReadableMode();
    public static final ToStringMode BYTE = new ByteMode();

    private static final int bufferSize = 1024 * 8;

    private final long size_;
    private ToStringMode toStringMode_ = DETAIL;
    private BaseType baseType_ = BaseType.BINARY;

    public ByteSize(final long size) {
        size_ = size;
    }

    public static ByteSize create(final InputStream is) {
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
            return new ByteSize(l);
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
        return toString(HUMAN_READABLE);
    }

    @Override
    public String toString() {
        return toString(toStringMode_);
    }

    private String toString(final ToStringMode toStringMode) {
        return toStringMode.toString(this);
    }

    public void setToStringMode(final ToStringMode toStringMode) {
        if (toStringMode == null) {
            throw new NullPointerException("toStringMode");
        }
        toStringMode_ = toStringMode;
    }

    public BaseType getBaseType() {
        return baseType_;
    }

    public void setBaseType(final BaseType baseType) {
        baseType_ = baseType;
    }

    public static enum BaseType {

        BINARY(ByteSizeUnits.BinaryUnits.getInstance()),

        DECIMAL(ByteSizeUnits.DecimalUnits.getInstance())

        ;

        private final ByteSizeUnits.UnitsTable unitsTable_;

        private BaseType(final ByteSizeUnits.UnitsTable unitsTable) {
            unitsTable_ = unitsTable;
        }

        public ByteSizeUnits.UnitsTable getUnitsTable() {
            return unitsTable_;
        }

    }

    public static interface ToStringMode {

        String toString(ByteSize byteSize);

    }

    static abstract class AbstractToStringMode implements ToStringMode {

        protected void appendTo(final ByteSizeUnit unit, final long size,
                final StringBuilder sb) {
            sb.append(unit.format(size));
            if (unit == ByteSizeUnits.B) {
                return;
            }
            sb.append(" ");
            sb.append(unit.getUnitLabel());
        }

    }

    static class DetailMode extends AbstractToStringMode {

        @Override
        public String toString(final ByteSize byteSize) {
            final long size = byteSize.getSize();
            final ByteSizeUnit unit = byteSize.getBaseType().getUnitsTable()
                    .detectUnit(size);
            final StringBuilder sb = new StringBuilder();
            appendTo(unit, size, sb);
            if (unit == ByteSizeUnits.B) {
                return sb.toString();
            }

            sb.append(" (");
            appendTo(ByteSizeUnits.B, size, sb);
            sb.append(")");
            return sb.toString();
        }

    }

    static class HumanReadableMode extends AbstractToStringMode {

        @Override
        public String toString(final ByteSize byteSize) {
            final long size = byteSize.getSize();
            final ByteSizeUnit unit = byteSize.getBaseType().getUnitsTable()
                    .detectUnit(size);
            final StringBuilder sb = new StringBuilder();
            appendTo(unit, size, sb);
            return sb.toString();
        }

    }

    static class ByteMode extends AbstractToStringMode {

        @Override
        public String toString(final ByteSize byteSize) {
            final long size = byteSize.getSize();
            final StringBuilder sb = new StringBuilder();
            appendTo(ByteSizeUnits.B, size, sb);
            return sb.toString();
        }

    }

}
