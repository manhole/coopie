package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.ToStringFormat;

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
    private String name;

    /**
     * CSVの項目名
     */
    private String label;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    @Override
    public boolean labelEquals(final String label) {
        return this.label.equals(label);
    }

    @Override
    public String toString() {
        return new ToStringFormat().format(this);
    }

}
