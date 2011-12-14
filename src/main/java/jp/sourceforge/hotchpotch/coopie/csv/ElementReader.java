package jp.sourceforge.hotchpotch.coopie.csv;

import jp.sourceforge.hotchpotch.coopie.util.Closable;

public interface ElementReader extends Closable {

    /**
     * レコード番号を返します。
     * 初期値は0です。
     * {@link #readRecord()}で1件目を取得した後は、1を返すようになります。
     * 同様に、10件目を取得した後は10を返します。
     * {@link #readRecord()}で最後まで読むと、それ以上大きい値を返さないようになります。
     * 
     * レコードが複数行に渡る可能性がある場合は、テキストの行番号とは異なる値となります。
     * (テキストの行番号ではなく、データ番号)
     */
    int getRecordNo();

    /**
     * 1レコードを読んで返します。
     * 末端まで読んだ場合はnullを返すようになります。
     */
    String[] readRecord();

}
