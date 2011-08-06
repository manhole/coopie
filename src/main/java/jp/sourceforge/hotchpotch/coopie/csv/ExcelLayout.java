package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

public interface ExcelLayout<T> {

    CsvReader<T> openReader(InputStream is);

    CsvWriter<T> openWriter(OutputStream os);

}
