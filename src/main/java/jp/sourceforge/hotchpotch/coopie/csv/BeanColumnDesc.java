package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.PropertyDesc;

class BeanColumnDesc<T> implements ColumnDesc<T> {

    /**
     * CSV列名。
     */
    private ColumnName name;

    private PropertyDesc<T> propertyDesc;

    public ColumnName getName() {
        return name;
    }

    public void setName(final ColumnName name) {
        this.name = name;
    }

    public PropertyDesc<T> getPropertyDesc() {
        return propertyDesc;
    }

    public void setPropertyDesc(final PropertyDesc<T> propertyDesc) {
        this.propertyDesc = propertyDesc;
    }

    public String getValue(final T bean) {
        final Object v = propertyDesc.getValue(bean);
        // TODO null値の場合
        return String.valueOf(v);
    }

    public void setValue(final T bean, final String value) {
        propertyDesc.setValue(bean, value);
    }

}
