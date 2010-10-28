package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.Closable;

public interface CsvReader<T> extends Closable {

    T read();

    void read(T bean);

    boolean hasNext();

}
