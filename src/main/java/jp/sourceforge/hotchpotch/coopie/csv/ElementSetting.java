package jp.sourceforge.hotchpotch.coopie.csv;

public interface ElementSetting {

    ElementWriter openWriter(Appendable appendable);

    ElementReader openReader(Readable readable);

}
