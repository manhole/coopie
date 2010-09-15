package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.Closable;

public interface CsvReader<T> extends Closable {

    void read(T bean);

    boolean hasNext();

}
