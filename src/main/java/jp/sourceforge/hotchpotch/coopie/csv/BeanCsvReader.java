package jp.sourceforge.hotchpotch.coopie.csv;


public class BeanCsvReader<T> extends AbstractCsvReader<T> {

    public BeanCsvReader(final Class<T> beanClass) {
        csvLayout = new BeanCsvLayout<T>(beanClass);
    }

    public BeanCsvReader(final CsvLayout<T> columnLayout) {
        csvLayout = columnLayout;
    }

}
