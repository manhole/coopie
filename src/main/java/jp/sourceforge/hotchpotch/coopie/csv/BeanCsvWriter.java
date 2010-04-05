package jp.sourceforge.hotchpotch.coopie.csv;

public class BeanCsvWriter<T> extends AbstractCsvWriter<T> {

    public BeanCsvWriter(final Class<T> beanClass) {
        columnLayout = new BeanColumnLayout<T>(beanClass);
    }

    public BeanCsvWriter(final BeanColumnLayout<T> columnLayout) {
        this.columnLayout = columnLayout;
    }

}
