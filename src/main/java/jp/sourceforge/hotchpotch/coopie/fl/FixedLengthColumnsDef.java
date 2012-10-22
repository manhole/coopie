package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.Converter;

/*
 * @FixedLengthColumnsと対になる
 * 
 * TODO @FixedLengthColumnsを作る
 */
public interface FixedLengthColumnsDef {

    List<FixedLengthColumnDef> getColumnDefs();

    String getPropertyName();

    void setPropertyName(String propertyName);

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

}
