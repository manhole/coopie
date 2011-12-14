package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

public interface ExcelInOut<T> {

    RecordReader<T> openReader(InputStream is);

    RecordWriter<T> openWriter(OutputStream os);

}
