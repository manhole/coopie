package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public interface ElementSetting {

    CsvElementWriter openWriter(Writer writer);

    CsvElementReader openReader(Reader reader);

}
