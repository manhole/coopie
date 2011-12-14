package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public interface CsvLayout<T> {

    RecordReader<T> openReader(Reader reader);

    RecordWriter<T> openWriter(Writer writer);

}
