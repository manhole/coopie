package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.Closable;

public interface CsvElementWriter extends Closable {

    void writeLine(String[] line);

}