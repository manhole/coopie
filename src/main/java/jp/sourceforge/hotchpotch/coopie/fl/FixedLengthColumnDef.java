package jp.sourceforge.hotchpotch.coopie.fl;

/*
 * @FixedLengthColumnと対になる
 */
public interface FixedLengthColumnDef {

    String getName();

    int getBeginIndex();

    int getEndIndex();

}
