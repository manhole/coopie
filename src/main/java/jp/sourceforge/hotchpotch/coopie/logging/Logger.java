package jp.sourceforge.hotchpotch.coopie.logging;

public interface Logger extends org.slf4j.Logger {

    void debug(Log log);

    void warn(Log log);

}
