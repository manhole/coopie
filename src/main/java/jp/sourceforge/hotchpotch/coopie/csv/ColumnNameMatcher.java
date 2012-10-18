package jp.sourceforge.hotchpotch.coopie.csv;

/*
 * setupByHeaderで使う
 */
public interface ColumnNameMatcher {

    boolean matches(ColumnName columnName, String str);

}