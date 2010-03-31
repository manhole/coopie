package jp.sourceforge.hotchpotch.coopie;

import java.io.Closeable;

public interface Closable extends Closeable {

    boolean isClosed();

}
