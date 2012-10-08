package jp.sourceforge.hotchpotch.coopie.fl;

/*
 * @FixedLengthColumnと対になる
 */
public interface FixedLengthColumnDef {

    String getPropertyName();

    int getBeginIndex();

    int getEndIndex();

}
