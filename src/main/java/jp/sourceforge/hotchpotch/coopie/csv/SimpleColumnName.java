package jp.sourceforge.hotchpotch.coopie.csv;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

}
