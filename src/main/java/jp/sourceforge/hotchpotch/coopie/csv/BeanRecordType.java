package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.BeanDesc;

class BeanRecordType<T> implements RecordType<T> {

    private final BeanDesc<T> beanDesc;

    public BeanRecordType(final BeanDesc<T> beanDesc) {
        this.beanDesc = beanDesc;
    }

    @Override
    public T newInstance() {
        return beanDesc.newInstance();
    }

}
