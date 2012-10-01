package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

/*
 * @CsvColumnsと対になる
 */
public interface CsvColumnsDef {

    //    String getLabel();
    //
    //    void setLabel(String label);
    //
    //    int getOrder();
    //
    //    void setOrder(int order);

    List<CsvColumnDef> getColumnDefs();

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

    String getPropertyName();

    Class<?> getPropertyType();

}
