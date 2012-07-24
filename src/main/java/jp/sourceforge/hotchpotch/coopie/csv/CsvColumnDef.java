package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.PropertyDesc;

/*
 * @CsvColumnと対になる
 */
public interface CsvColumnDef<BEAN> {

    String getLabel();

    void setLabel(String label);

    int getOrder();

    void setOrder(int order);

    Converter<?, ?> getConverter();

    void setConverter(Converter<?, ?> converter);

    PropertyDesc<BEAN> getPropertyDesc();

    void setPropertyDesc(PropertyDesc<BEAN> propertyDesc);

}
