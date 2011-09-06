package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;

public interface ElementSetting {

    ElementWriter openWriter(Writer writer);

    ElementReader openReader(Reader reader);

}
