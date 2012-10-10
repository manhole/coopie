package jp.sourceforge.hotchpotch.coopie.fl;

import java.util.List;

import jp.sourceforge.hotchpotch.coopie.csv.Converter;
import jp.sourceforge.hotchpotch.coopie.csv.PassthroughStringConverter;

import org.t2framework.commons.util.CollectionsUtil;

class DefaultFixedLengthColumnsDef implements FixedLengthColumnsDef {

    private String propertyName_;
    private Converter<?, ?> converter_ = PassthroughStringConverter
            .getInstance();
    private final List<FixedLengthColumnDef> columnDefs_ = CollectionsUtil
            .newArrayList();

    @Override
    public List<FixedLengthColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    public void addColumnDef(final FixedLengthColumnDef columnDef) {
        columnDefs_.add(columnDef);
    }

    @Override
    public String getPropertyName() {
        return propertyName_;
    }

    public void setPropertyName(final String propertyName) {
        propertyName_ = propertyName;
    }

    @Override
    public Converter<?, ?> getConverter() {
        return converter_;
    }

    @Override
    public void setConverter(final Converter<?, ?> converter) {
        converter_ = converter;
    }

}
