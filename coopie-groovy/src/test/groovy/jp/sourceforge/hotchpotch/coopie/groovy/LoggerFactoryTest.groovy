package jp.sourceforge.hotchpotch.coopie.groovy

import org.junit.Test

import jp.sourceforge.hotchpotch.coopie.logging.Logger
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory

class LoggerFactoryTest {

    private static final Logger logger1 = LoggerFactory.getLogger()
    private static final Logger logger2 = LoggerFactory.getLogger(LoggerFactoryTest)
    
    /*
     * Groovy環境では
     * 
     * at jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory.getLogger(LoggerFactory.java:39)
     * at jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory$getLogger.call(Unknown Source)
     * at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:45)
     * at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:108)
     * at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:112)
     * at jp.sourceforge.hotchpotch.coopie.groovy.LoggerFactoryTest.<clinit>(LoggerFactoryTest.groovy:10)
     * 
     * と、これだけの呼び出しが間に入るため、Java環境と同じ方法ではダメ。
     */
    @Test
    public void loggerName() {
        // 残念ながら同じにならない
        assert logger1.name != LoggerFactoryTest.name
        assert logger2.name == LoggerFactoryTest.name
    }
}
