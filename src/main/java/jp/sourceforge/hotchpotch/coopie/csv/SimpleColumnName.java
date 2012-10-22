package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

public class SimpleColumnName implements ColumnName {

    /**
     * CSVの項目名
     */
    private String label_;

    private ColumnNameMatcher columnNameMatcher_ = ExactNameMatcher
            .getInstance();

    public SimpleColumnName() {
    }

    public SimpleColumnName(final String label) {
        setLabel(label);
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

}
