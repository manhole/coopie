package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.Closable;

public interface CsvElementReader extends Closable {

    String[] readRecord();

}
