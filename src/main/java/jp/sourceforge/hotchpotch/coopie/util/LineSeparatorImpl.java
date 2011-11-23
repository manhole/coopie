package jp.sourceforge.hotchpotch.coopie.util;

public class LineSeparatorImpl implements LineSeparator {

    private final String str_;
    private final String label_;

    public LineSeparatorImpl(final String str, final String label) {
        str_ = str;
        label_ = label;
    }

    @Override
    public String getSeparator() {
        return str_;
    }

    @Override
    public int hashCode() {
        return str_.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LineSeparatorImpl)) {
            return false;
        }
        final LineSeparatorImpl another = (LineSeparatorImpl) obj;
        return str_.equals(another.str_);
    }

    @Override
    public String toString() {
        return label_;
    }

}
