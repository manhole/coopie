package jp.sourceforge.hotchpotch.coopie.csv;

public class ContainsNameMatcher implements ColumnNameMatcher {

    private final String contained_;

    public ContainsNameMatcher(final String contained) {
        contained_ = contained;
    }

    @Override
    public boolean matches(final ColumnName columnName, final String str) {
        return str.contains(contained_);
    }

}
