package jp.sourceforge.hotchpotch.coopie;

public interface Callback<T, E extends Throwable> {

    void callback(T t) throws E;

}
