package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.BeanDesc;

class BeanRecordType<T> implements RecordType<T> {

    private final BeanDesc<T> beanDesc_;

    public BeanRecordType(final BeanDesc<T> beanDesc) {
        beanDesc_ = beanDesc;
    }

    @Override
    public T newInstance() {
        return beanDesc_.newInstance();
    }

}
