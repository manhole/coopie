package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.Closable;

public interface ElementReader extends Closable {

    int getRecordNo();

    String[] readRecord();

}
