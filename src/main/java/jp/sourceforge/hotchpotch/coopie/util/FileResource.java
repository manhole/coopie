package jp.sourceforge.hotchpotch.coopie.util;

public interface FileResource {

    /**
     * ファイルの拡張子を返します。
     * 
     * "foo.txt" → "txt"
     * "foo.bar.txt" → "txt"
     */
    String getPrefix();

    /**
     * ファイルの拡張子を返します。
     * 
     * "foo.txt" → "txt"
     * "foo.bar.txt" → "txt"
     */
    String getExtension();

}