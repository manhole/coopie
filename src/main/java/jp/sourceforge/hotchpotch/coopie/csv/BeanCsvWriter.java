package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.Closable;

public class BeanCsvWriter<T> extends AbstractCsvWriter<T> implements Closable {

    private final BeanColumnLayout<T> columnLayout;

    public BeanCsvWriter(final Class<T> beanClass) {
        columnLayout = new BeanColumnLayout<T>(beanClass);
    }

    public BeanCsvWriter(final BeanColumnLayout<T> columnLayout) {
        this.columnLayout = columnLayout;
    }

    protected String[] getValues(final T bean) {
        return columnLayout.getValues(bean);
    }

    @Override
    protected ColumnName[] getColumnNames() {
        return columnLayout.getNames();
    }

}
