package jp.sourceforge.hotchpotch.coopie.csv;

public class ExactNameMatcher implements ColumnNameMatcher {

    private static ExactNameMatcher INSTANCE = new ExactNameMatcher();

    public static ExactNameMatcher getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean matches(final ColumnName columnName, final String str) {
        return columnName.getLabel().equals(str);
    }

}
