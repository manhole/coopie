package jp.sourceforge.hotchpotch.coopie.csv;

class BeanCsvReader<T> extends DefaultCsvReader<T> {

    public BeanCsvReader(final CsvLayout<T> layout) {
        super(layout);
    }

}
