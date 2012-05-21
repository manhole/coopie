package jp.sourceforge.hotchpotch.coopie.csv;

public interface Converter {

    void convertTo(Object[] from, String[] to);

    void convertFrom(String[] from, Object[] to);

}
