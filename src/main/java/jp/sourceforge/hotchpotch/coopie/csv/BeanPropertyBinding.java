package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.PropertyDesc;

class BeanPropertyBinding<BEAN, PROP> implements PropertyBinding<BEAN, PROP> {

    private final PropertyDesc<BEAN> propertyDesc_;

    public BeanPropertyBinding(final PropertyDesc<BEAN> propertyDesc) {
        propertyDesc_ = propertyDesc;
    }

    @Override
    public void setValue(final BEAN bean, final PROP value) {
        propertyDesc_.setValue(bean, value);
    }

    @Override
    public PROP getValue(final BEAN bean) {
        final Object value = propertyDesc_.getValue(bean);
        @SuppressWarnings("unchecked")
        final PROP v = (PROP) value;
        return v;
    }

}
