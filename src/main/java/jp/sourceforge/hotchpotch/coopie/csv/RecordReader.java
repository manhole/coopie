package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Closable;

public interface RecordReader<T> extends Closable {

    T read();

    void read(T bean);

    boolean hasNext();

}
