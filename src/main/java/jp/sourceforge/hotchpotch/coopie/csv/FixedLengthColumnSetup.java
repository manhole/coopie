package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractLayout.RecordDescSetup;

public interface FixedLengthColumnSetup<T> extends RecordDescSetup<T> {

    void column(String propertyName, int beginIndex, int endIndex);

}
