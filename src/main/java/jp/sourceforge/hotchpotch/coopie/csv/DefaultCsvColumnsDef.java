package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.List;

import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.CollectionsUtil;
import org.t2framework.commons.util.StringUtil;

class DefaultCsvColumnsDef implements CsvColumnsDef {

    private String propertyName_;
    private Class<?> propertyType_;
    private Converter<?, ?> converter_ = PassthroughStringConverter
            .getInstance();
    private final List<CsvColumnDef> columnDefs_ = CollectionsUtil
            .newArrayList();

    public void setup(final CsvColumns columns, final PropertyDesc pd) {
        for (final CsvColumn column : columns.value()) {
            final DefaultCsvColumnDef columnDef = new DefaultCsvColumnDef();
            if (StringUtil.isBlank(column.label())) {
                columnDef.setLabel(pd.getPropertyName());
            } else {
                columnDef.setLabel(column.label());
            }
            columnDef.setOrder(column.order());
            addColumnDef(columnDef);
        }
        setPropertyName(pd.getPropertyName());
        setPropertyType(pd.getPropertyType());
    }

    @Override
    public List<CsvColumnDef> getColumnDefs() {
        return columnDefs_;
    }

    public void addColumnDef(final CsvColumnDef columnDef) {
        columnDefs_.add(columnDef);
    }

    @Override
    public boolean hasConverter() {
        if (converter_ == null) {
            return false;
        }
        if (converter_ instanceof PassthroughStringConverter) {
            return false;
        }
        return true;
    }

    @Override
    public Converter<?, ?> getConverter() {
        return converter_;
    }

    @Override
    public void setConverter(final Converter<?, ?> converter) {
        converter_ = converter;
    }

    @Override
    public String getPropertyName() {
        return propertyName_;
    }

    @Override
    public void setPropertyName(final String propertyName) {
        propertyName_ = propertyName;
    }

    @Override
    public Class<?> getPropertyType() {
        return propertyType_;
    }

    public void setPropertyType(final Class<?> propertyType) {
        propertyType_ = propertyType;
    }

}
