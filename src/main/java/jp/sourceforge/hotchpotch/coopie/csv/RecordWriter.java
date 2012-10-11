package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Closable;

public interface RecordWriter<BEAN> extends Closable {

    void write(BEAN bean);

}
