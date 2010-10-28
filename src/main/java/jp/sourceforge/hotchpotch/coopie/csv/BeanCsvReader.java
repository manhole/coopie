package jp.sourceforge.hotchpotch.coopie.csv;

import org.t2framework.commons.meta.BeanDesc;

class BeanCsvReader<T> extends DefaultCsvReader<T> {

    private final BeanDesc<T> beanDesc;

    public BeanCsvReader(final RecordDesc<T> layout, final BeanDesc<T> beanDesc) {
        super(layout);
        this.beanDesc = beanDesc;
    }

    @Override
    protected T newInstance() {
        return beanDesc.newInstance();
    }

}
