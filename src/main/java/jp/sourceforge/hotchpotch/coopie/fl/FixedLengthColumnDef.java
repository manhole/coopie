package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.ColumnNameMatcher;
import jp.sourceforge.hotchpotch.coopie.csv.Converter;

/*
 * @FixedLengthColumnと対になる
 */
public interface FixedLengthColumnDef {

    int getBeginIndex();

    int getEndIndex();

    String getPropertyName();

    void setPropertyName(String propertyName);

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

    ColumnNameMatcher getColumnNameMatcher();

    void setColumnNameMatcher(ColumnNameMatcher columnNameMatcher);

}
