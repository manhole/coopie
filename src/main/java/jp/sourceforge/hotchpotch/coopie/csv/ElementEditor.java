package jp.sourceforge.hotchpotch.coopie.csv;

public interface ElementEditor {

    /**
     * {@link ElementReaderHandler#readRecord(ElementReader)}で読んだ各要素を編集できます。
     */
    String edit(String element);

}
