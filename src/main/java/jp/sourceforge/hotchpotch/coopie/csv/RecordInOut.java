package jp.sourceforge.hotchpotch.coopie.csv;

public interface RecordInOut<T> {

    RecordWriter<T> openWriter(Appendable appendable);

    RecordReader<T> openReader(Readable readable);

}
