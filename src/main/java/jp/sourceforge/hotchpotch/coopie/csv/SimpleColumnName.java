package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

public class SimpleColumnName implements ColumnName {

    public SimpleColumnName() {
    }

    public SimpleColumnName(final String labelAndName) {
        setLabel(labelAndName);
        setName(labelAndName);
    }

    private ColumnNameMatcher columnNameMatcher_ = ExactNameMatcher
            .getInstance();

    /**
     * オブジェクトのプロパティ名
     */
    private String name_;

    /**
     * CSVの項目名
     */
    private String label_;

    public String getName() {
        return name_;
    }

    public void setName(final String name) {
        name_ = name;
    }

    @Override
    public String getLabel() {
        return label_;
    }

    public void setLabel(final String label) {
        label_ = label;
    }

    @Override
    public boolean labelEquals(final String label) {
        return columnNameMatcher_.matches(this, label);
    }

    public void setColumnNameMatcher(final ColumnNameMatcher columnNameMatcher) {
        columnNameMatcher_ = columnNameMatcher;
    }

    @Override
    public String toString() {
        return new ToStringFormat().format(this);
    }

    private static class ExactNameMatcher implements ColumnNameMatcher {

        private static ExactNameMatcher INSTANCE = new ExactNameMatcher();

        public static ExactNameMatcher getInstance() {
            return INSTANCE;
        }

        @Override
        public boolean matches(final ColumnName columnName, final String str) {
            return columnName.getLabel().equals(str);
        }

    }

}
