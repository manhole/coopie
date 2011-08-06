package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public interface CsvLayout<T> {

    CsvReader<T> openReader(Reader reader);

    CsvWriter<T> openWriter(Writer writer);

}
