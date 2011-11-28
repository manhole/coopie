package jp.sourceforge.hotchpotch.coopie.csv;

public interface ElementStream {

    ElementWriter openWriter(Appendable appendable);

    ElementReader openReader(Readable readable);

}
