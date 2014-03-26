/*
 * Copyright 2010 manhole
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

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
