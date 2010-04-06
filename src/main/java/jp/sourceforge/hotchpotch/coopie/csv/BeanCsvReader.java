package jp.sourceforge.hotchpotch.coopie.csv;

class BeanCsvReader<T> extends DefaultCsvReader<T> {

    public BeanCsvReader(final RecordDesc<T> layout) {
        super(layout);
    }

}
