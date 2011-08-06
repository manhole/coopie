package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractFixedLengthLayout.FixedLengthRecordDescSetup;

public interface FixedLengthColumnSetup<T> extends
        FixedLengthRecordDescSetup<T> {

    void column(String propertyName, int beginIndex, int endIndex);

}
