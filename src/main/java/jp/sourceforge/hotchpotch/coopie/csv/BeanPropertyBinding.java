package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.MethodDesc;
import org.t2framework.commons.meta.PropertyDesc;

class BeanPropertyBinding<BEAN, PROP> implements PropertyBinding<BEAN, PROP> {

    private final MethodDesc writeMethodDesc_;
    private final MethodDesc readMethodDesc_;

    public BeanPropertyBinding(final PropertyDesc<BEAN> propertyDesc) {
        writeMethodDesc_ = propertyDesc.getWriteMethodDesc();
        readMethodDesc_ = propertyDesc.getReadMethodDesc();
    }

    @Override
    public void setValue(final BEAN bean, final PROP value) {
        /*
         * PropertyDesc#setValueだと勝手に変換されるので、
         * 変換されないようMethodDescを取り出して使用する。
         */
        writeMethodDesc_.invoke(bean, new Object[] { value });
    }

    @Override
    public PROP getValue(final BEAN bean) {
        final Object value = readMethodDesc_.invoke(bean, null);
        @SuppressWarnings("unchecked")
        final PROP v = (PROP) value;
        return v;
    }

}
