package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvLayout<T> {

    RecordReader<T> openReader(Readable readable);

    RecordWriter<T> openWriter(Appendable appendable);

}
