package jp.sourceforge.hotchpotch.coopie.csv;

public interface RecordInOut<BEAN> {

    RecordReader<BEAN> openReader(Readable readable);

    RecordWriter<BEAN> openWriter(Appendable appendable);

}
