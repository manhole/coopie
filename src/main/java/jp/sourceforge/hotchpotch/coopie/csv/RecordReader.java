package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Closable;

public interface RecordReader<BEAN> extends Closable {

    BEAN read();

    void read(BEAN bean);

    boolean hasNext();

    /**
     * レコード番号を返します。
     * 初期値は0です。
     * {@link #read()}で1件目を取得した後は、1を返すようになります。
     * 同様に、10件目を取得した後は10を返します。
     * 最後まで読むと、それ以上大きい値を返さないようになります。
     */
    int getRecordNumber();

}
