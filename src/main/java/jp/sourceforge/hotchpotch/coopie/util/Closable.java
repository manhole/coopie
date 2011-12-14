package jp.sourceforge.hotchpotch.coopie.util;

import java.io.Closeable;

public interface Closable extends Closeable {

    boolean isClosed();

}
