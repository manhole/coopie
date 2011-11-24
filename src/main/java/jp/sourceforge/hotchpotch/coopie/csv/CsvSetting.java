package jp.sourceforge.hotchpotch.coopie.csv;

public interface CsvSetting {

    char TAB = '\t';
    char COMMA = ',';
    char DOUBLE_QUOTE = '\"';
    char CR = '\r';
    char LF = '\n';
    String CR_S = Character.toString(CR);
    String LF_S = Character.toString(LF);
    String CRLF = CR_S + LF_S;

    String getLineSeparator();

    void setLineSeparator(final String lineSeparator);

    char getQuoteMark();

    void setQuoteMark(final char quoteMark);

    char getElementSeparator();

    void setElementSeparator(final char elementSeparator);

}
