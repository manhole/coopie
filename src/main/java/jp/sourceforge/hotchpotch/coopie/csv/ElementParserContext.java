package jp.sourceforge.hotchpotch.coopie.csv;

public interface ElementParserContext {

    /**
     * 要素の途中であるかを返します。
     * trueの場合は、要素の途中で次行へ進んだことを意味します。(改行を含む要素)
     * 
     * @return trueの場合、要素の最中であることを示します。
     * falseの場合、要素やレコードの区切り(要素の最中ではない)であることを示します。
     */
    boolean isInElement();

}
