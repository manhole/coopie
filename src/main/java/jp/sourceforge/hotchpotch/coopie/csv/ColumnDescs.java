package jp.sourceforge.hotchpotch.coopie.csv;

class ColumnDescs {

    @SuppressWarnings("unchecked")
    protected static <U> ColumnDesc<U>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

}
