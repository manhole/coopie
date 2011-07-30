package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public interface ElementSetting {

    public CsvElementWriter openWriter(Writer writer);

    public CsvElementReader openReader(Reader reader);

}
