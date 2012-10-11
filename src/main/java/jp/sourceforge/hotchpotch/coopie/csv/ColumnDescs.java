package jp.sourceforge.hotchpotch.coopie.csv;

public class ColumnDescs {

    @SuppressWarnings("unchecked")
    public static <BEAN> ColumnDesc<BEAN>[] newColumnDescs(final int length) {
        return new ColumnDesc[length];
    }

}
