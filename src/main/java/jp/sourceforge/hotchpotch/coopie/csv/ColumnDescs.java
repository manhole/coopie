package jp.sourceforge.hotchpotch.coopie.csv;

public class ColumnDescs {

    @SuppressWarnings("unchecked")
    public static <U> ColumnDesc<U>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

}
