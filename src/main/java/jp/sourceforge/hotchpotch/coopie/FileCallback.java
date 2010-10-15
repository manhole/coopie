package jp.sourceforge.hotchpotch.coopie;

import java.io.File;
import java.io.IOException;

public interface FileCallback {

    void callback(File file) throws IOException;

}
