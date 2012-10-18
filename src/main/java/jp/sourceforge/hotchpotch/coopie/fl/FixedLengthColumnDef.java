package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.Converter;

/*
 * @FixedLengthColumnと対になる
 */
public interface FixedLengthColumnDef {

    String getPropertyName();

    int getBeginIndex();

    int getEndIndex();

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

}
