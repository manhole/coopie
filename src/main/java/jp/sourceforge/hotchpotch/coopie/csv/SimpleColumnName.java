package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.ToStringFormat;

public class SimpleColumnName implements ColumnName {

    public SimpleColumnName() {
    }

    public SimpleColumnName(final String labelAndName) {
        setLabel(labelAndName);
        setName(labelAndName);
    }

    /**
     * オブジェクトのプロパティ名
     */
    private String name_;

    /**
     * CSVの項目名
     */
    private String label_;

    @Override
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
        return label_.equals(label);
    }

    @Override
    public String toString() {
        return new ToStringFormat().format(this);
    }

}
