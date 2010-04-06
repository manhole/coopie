package jp.sourceforge.hotchpotch.coopie.csv;

public class BeanCsvWriter<T> extends AbstractCsvWriter<T> {

    public BeanCsvWriter(final Class<T> beanClass) {
        csvLayout = new BeanCsvLayout<T>(beanClass);
    }

    public BeanCsvWriter(final BeanCsvLayout<T> columnLayout) {
        this.csvLayout = columnLayout;
    }

}
