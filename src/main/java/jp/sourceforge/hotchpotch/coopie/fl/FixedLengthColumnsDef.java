package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.Converter;

/*
 * @FixedLengthColumnsと対になる
 * 
 * TODO @FixedLengthColumnsを作る
 */
public interface FixedLengthColumnsDef {

    String getPropertyName();

    List<FixedLengthColumnDef> getColumnDefs();

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

}
