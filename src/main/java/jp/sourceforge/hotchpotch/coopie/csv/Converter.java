package jp.sourceforge.hotchpotch.coopie.csv;

/*
 * IN ... JavaBean
 * OUT ... CSV, FixedLength, Excel
 */
public interface Converter<IN, OUT> {

    OUT convertTo(IN from);

    IN convertFrom(OUT from);

}
