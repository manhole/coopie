package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;

public interface LineReader extends Closable {

    int getLineNumber();

    Line readLine() throws IOException;

    Line readLine(Line reusableLine) throws IOException;

}
