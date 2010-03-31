package jp.sourceforge.hotchpotch.coopie;

import org.slf4j.Logger;

public class LoggerFactory {

    private static final String MY_CLASS_NAME = LoggerFactory.class.getName();
    /*
     * org.seasar.framework.aop.javassist.AspectWeaver.SUFFIX_ENHANCED_CLASS
     */
    private static final String SUFFIX_ENHANCED_CLASS = "$$EnhancedByS2AOP$$";

    public static Logger getLogger() {
        final String className = getCallerClassName();
        final Logger logger = getLogger(className);
        return logger;
    }

    private static Logger getLogger(final String name) {
        final Logger logger = org.slf4j.LoggerFactory.getLogger(name);
        return logger;
    }

    /*
     * LoggerFactoryを呼んだクラス名
     */
    static String getCallerClassName() {
        final Thread currentThread = Thread.currentThread();
        final StackTraceElement[] stackTraceElements = currentThread
            .getStackTrace();
        boolean inMyClass = false;
        for (final StackTraceElement element : stackTraceElements) {
            final String className = element.getClassName();
            if (inMyClass) {
                if (!className.equals(MY_CLASS_NAME)) {
                    return cleanClassName(className);
                }
            } else {
                if (className.equals(MY_CLASS_NAME)) {
                    inMyClass = true;
                }
            }
        }
        // あり得ない
        throw new AssertionError();
    }

    public static String cleanClassName(final String className) {
        // S2Aopに拡張されたクラス名を避ける。
        final int pos = className.indexOf(SUFFIX_ENHANCED_CLASS);
        if (-1 < pos) {
            return className.substring(0, pos);
        }
        return className;
    }

}
