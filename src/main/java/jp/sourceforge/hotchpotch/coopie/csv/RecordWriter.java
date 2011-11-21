package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Closable;

public interface RecordWriter<T> extends Closable {

    void write(T bean);

}
