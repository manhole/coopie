package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.InputStream;
import java.io.OutputStream;

public interface ExcelInOut<BEAN> {

    RecordReader<BEAN> openReader(InputStream is);

    RecordWriter<BEAN> openWriter(OutputStream os);

}
