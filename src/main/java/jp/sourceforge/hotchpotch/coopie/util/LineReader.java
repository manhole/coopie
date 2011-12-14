package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;

public interface LineReader {

    int getLineNumber();

    Line readLine() throws IOException;

}
