package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReader;

public interface LineReaderHandler {

    /**
     * 1行読むタイミングで呼ばれます。
     * 行へ手を入れたい場合は当メソッドにて変更して返却してください。
     * 
     */
    // 当メソッドが返却した値が、 {@link #readRecord(ElementReader)} へ流れていきます。
    // (当メソッドにて読み飛ばした行は、{@link #readRecord(ElementReader)} へ流れません。)
    Line readLine(LineReader lineReader, Line reusableLine) throws IOException;

    /**
     * 1行読むタイミングで呼ばれます。
     * 
     * 行を使用するかを判定します。
     * trueを返すとこの行は採用されます。
     * falseを返すとこの行はskipされます。
     * 
     */
    // 当メソッドがtrueを返した行が、 {@link #readRecord(ElementReader)} へ流れていきます。
    boolean acceptLine(Line line, ElementParserContext parserContext);

}
