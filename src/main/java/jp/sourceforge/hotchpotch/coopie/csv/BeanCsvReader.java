package jp.sourceforge.hotchpotch.coopie.csv;

public class BeanCsvReader<T> extends DefaultCsvReader<T> {

    public BeanCsvReader(final Class<T> beanClass) {
        this(new BeanCsvLayout<T>(beanClass));
    }

    public BeanCsvReader(final CsvLayout<T> layout) {
        super(layout);
    }

}
