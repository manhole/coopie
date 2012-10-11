package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.BeanDesc;

public class BeanRecordType<BEAN> implements RecordType<BEAN> {

    private final BeanDesc<BEAN> beanDesc_;

    public BeanRecordType(final BeanDesc<BEAN> beanDesc) {
        beanDesc_ = beanDesc;
    }

    @Override
    public BEAN newInstance() {
        return beanDesc_.newInstance();
    }

}
