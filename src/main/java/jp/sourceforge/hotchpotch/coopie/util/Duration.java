package jp.sourceforge.hotchpotch.coopie.util;

public interface Duration {

    long getBegin();

    long getEnd();

    /**
     * END - BEGIN
     */
    long getElapsed();

}
