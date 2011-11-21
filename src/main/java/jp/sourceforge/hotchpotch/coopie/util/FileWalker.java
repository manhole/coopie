package jp.sourceforge.hotchpotch.coopie.util;

import java.io.File;

public interface FileWalker {

    /*
     * ディレクトリへ入るかどうか
     * 
     * falseを返すとそれ以上深いディレクトリへはenterしない。
     */
    boolean shouldEnter(File dir);

    /*
     * ディレクトリへ入る。
     * 
     * shouldEnterでtrueを返すと直後に呼ばれる。
     */
    void enter(File dir);

    /*
     * ディレクトリから出る
     */
    void leave(File dir);

    /*
     * enterしたディレクトリ内のファイル。
     * 引数にはファイルを渡す。ディレクトリは渡さない。
     */
    void file(File file);

}
