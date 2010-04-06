package jp.sourceforge.hotchpotch.coopie.csv;

class BeanCsvWriter<T> extends DefaultCsvWriter<T> {

    public BeanCsvWriter(final BeanCsvLayout<T> layout) {
        super(layout);
    }

}
