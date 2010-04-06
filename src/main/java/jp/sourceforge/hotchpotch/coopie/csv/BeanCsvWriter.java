package jp.sourceforge.hotchpotch.coopie.csv;

public class BeanCsvWriter<T> extends DefaultCsvWriter<T> {

    public BeanCsvWriter(final Class<T> beanClass) {
        this(new BeanCsvLayout<T>(beanClass));
    }

    public BeanCsvWriter(final BeanCsvLayout<T> layout) {
        super(layout);
    }

}
