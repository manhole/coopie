package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

/*
 * @CsvColumnsと対になる
 */
public interface CsvColumnsDef {

    List<CsvColumnDef> getColumnDefs();

    boolean hasConverter();

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

    String getPropertyName();

    void setPropertyName(String propertyName);

    Class<?> getPropertyType();

}
