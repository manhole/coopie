package jp.sourceforge.hotchpotch.coopie.csv;

public interface ReadEditor extends LineReaderHandler {

    /**
     * 1行読むタイミングで呼ばれます。
     * 行へ手を入れたい場合は当メソッドにて変更して返却してください。
     */
    String[] readRecord(ElementReader elementReader);

}
