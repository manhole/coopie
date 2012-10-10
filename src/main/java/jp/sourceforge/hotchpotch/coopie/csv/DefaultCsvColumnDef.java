package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.PropertyDesc;
import org.t2framework.commons.util.StringUtil;

class DefaultCsvColumnDef implements CsvColumnDef, Comparable<CsvColumnDef> {

    private String label_;
    private String propertyName_;
    private Class<?> propertyType_;

    private int order_;
    private Converter<?, ?> converter_ = PassthroughStringConverter
            .getInstance();

    private ColumnName columnName_;

    public void setup(final CsvColumn column, final PropertyDesc pd) {
        if (StringUtil.isBlank(column.label())) {
            setLabel(pd.getPropertyName());
        } else {
            setLabel(column.label());
        }
        setOrder(column.order());
        setPropertyName(pd.getPropertyName());
        setPropertyType(pd.getPropertyType());
    }

    public void setup(final PropertyDesc pd) {
        final String propertyName = pd.getPropertyName();
        setLabel(propertyName);
        setPropertyName(pd.getPropertyName());
        setPropertyType(pd.getPropertyType());
    }

    @Override
    public String getLabel() {
        return label_;
    }

    @Override
    public void setLabel(final String label) {
        label_ = label;
    }

    @Override
    public int getOrder() {
        return order_;
    }

    @Override
    public void setOrder(final int order) {
        order_ = order;
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

    @Override
    public int compareTo(final CsvColumnDef o) {
        // orderが小さい方を左側に
        final int ret = getOrder() - o.getOrder();
        return ret;
    }

    @Override
    public ColumnName getColumnName() {
        if (columnName_ == null) {
            final SimpleColumnName columnName = new SimpleColumnName();
            columnName.setName(getPropertyName());
            columnName.setLabel(getLabel());
            columnName_ = columnName;
        }
        return columnName_;
    }

    @Override
    public void setColumnName(final ColumnName columnName) {
        columnName_ = columnName;
    }

}
