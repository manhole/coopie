package jp.sourceforge.hotchpotch.coopie.csv;

class BeanCsvWriter<T> extends DefaultCsvWriter<T> {

    public BeanCsvWriter(final RecordDesc<T> layout) {
        super(layout);
    }

}
