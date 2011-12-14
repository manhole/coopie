package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

interface LineReaderHandler {

    /**
     * 1行読むタイミングで呼ばれます。
     * 行へ手を入れたい場合は当メソッドにて変更して返却してください。
     * 
     */
    // 当メソッドが返却した値が、 {@link #readRecord(ElementReader)} へ流れていきます。
    // (当メソッドにて読み飛ばした行は、{@link #readRecord(ElementReader)} へ流れません。)
    Line readLine(LineReader lineReader) throws IOException;

}
