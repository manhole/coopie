package jp.sourceforge.hotchpotch.coopie.util;

public interface Callback<T, E extends Throwable> {

    void callback(T t) throws E;

}
