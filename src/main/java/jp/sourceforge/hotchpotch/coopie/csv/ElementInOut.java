package jp.sourceforge.hotchpotch.coopie.csv;

public interface ElementInOut {

    ElementWriter openWriter(Appendable appendable);

    ElementReader openReader(Readable readable);

}
