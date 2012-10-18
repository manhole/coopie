package jp.sourceforge.hotchpotch.coopie.csv;

/*
 * @CsvColumnと対になる
 */
public interface CsvColumnDef {

    String getLabel();

    void setLabel(String label);

    int getOrder();

    void setOrder(int order);

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

    String getPropertyName();

    void setPropertyName(String propertyName);

    Class<?> getPropertyType();

    ColumnNameMatcher getColumnNameMatcher();

    void setColumnNameMatcher(ColumnNameMatcher columnNameMatcher);

}
