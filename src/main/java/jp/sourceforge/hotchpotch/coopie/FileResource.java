package jp.sourceforge.hotchpotch.coopie;

public interface FileResource {

    /**
     * ファイルの拡張子を返します。
     * 
     * "foo.txt" → "txt"
     * "foo.bar.txt" → "txt"
     */
    String getExtension();

}
