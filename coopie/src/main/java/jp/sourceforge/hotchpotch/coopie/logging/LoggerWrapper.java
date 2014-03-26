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

package jp.sourceforge.hotchpotch.coopie.logging;

import org.slf4j.Marker;

public class LoggerWrapper implements Logger {

    private final org.slf4j.Logger logger_;

    public LoggerWrapper(final org.slf4j.Logger logger) {
        logger_ = logger;
    }

    @Override
    public void debug(final Log log) {
        if (log == null) {
            logger_.debug((String) null);
            return;
        }
        logger_.debug(log.getFormat(), log.getArgs());
    }

    @Override
    public void warn(final Log log) {
        if (log == null) {
            logger_.debug((String) null);
            return;
        }
        logger_.warn(log.getFormat(), log.getArgs());
    }

    // ==== delegate methods ====

    @Override
    public String getName() {
        return logger_.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger_.isTraceEnabled();
    }

    @Override
    public void trace(final String msg) {
        logger_.trace(msg);
    }

    @Override
    public void trace(final String format, final Object arg) {
        logger_.trace(format, arg);
    }

    @Override
    public void trace(final String format, final Object arg1, final Object arg2) {
        logger_.trace(format, arg1, arg2);
    }

    @Override
    public void trace(final String format, final Object[] argArray) {
        logger_.trace(format, argArray);
    }

    @Override
    public void trace(final String msg, final Throwable t) {
        logger_.trace(msg, t);
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return logger_.isTraceEnabled(marker);
    }

    @Override
    public void trace(final Marker marker, final String msg) {
        logger_.trace(marker, msg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg) {
        logger_.trace(marker, format, arg);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object arg1, final Object arg2) {
        logger_.trace(marker, format, arg1, arg2);
    }

    @Override
    public void trace(final Marker marker, final String format, final Object[] argArray) {
        logger_.trace(marker, format, argArray);
    }

    @Override
    public void trace(final Marker marker, final String msg, final Throwable t) {
        logger_.trace(marker, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger_.isDebugEnabled();
    }

    @Override
    public void debug(final String msg) {
        logger_.debug(msg);
    }

    @Override
    public void debug(final String format, final Object arg) {
        logger_.debug(format, arg);
    }

    @Override
    public void debug(final String format, final Object arg1, final Object arg2) {
        logger_.debug(format, arg1, arg2);
    }

    @Override
    public void debug(final String format, final Object[] argArray) {
        logger_.debug(format, argArray);
    }

    @Override
    public void debug(final String msg, final Throwable t) {
        logger_.debug(msg, t);
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return logger_.isDebugEnabled(marker);
    }

    @Override
    public void debug(final Marker marker, final String msg) {
        logger_.debug(marker, msg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg) {
        logger_.debug(marker, format, arg);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object arg1, final Object arg2) {
        logger_.debug(marker, format, arg1, arg2);
    }

    @Override
    public void debug(final Marker marker, final String format, final Object[] argArray) {
        logger_.debug(marker, format, argArray);
    }

    @Override
    public void debug(final Marker marker, final String msg, final Throwable t) {
        logger_.debug(marker, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger_.isInfoEnabled();
    }

    @Override
    public void info(final String msg) {
        logger_.info(msg);
    }

    @Override
    public void info(final String format, final Object arg) {
        logger_.info(format, arg);
    }

    @Override
    public void info(final String format, final Object arg1, final Object arg2) {
        logger_.info(format, arg1, arg2);
    }

    @Override
    public void info(final String format, final Object[] argArray) {
        logger_.info(format, argArray);
    }

    @Override
    public void info(final String msg, final Throwable t) {
        logger_.info(msg, t);
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return logger_.isInfoEnabled(marker);
    }

    @Override
    public void info(final Marker marker, final String msg) {
        logger_.info(marker, msg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg) {
        logger_.info(marker, format, arg);
    }

    @Override
    public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
        logger_.info(marker, format, arg1, arg2);
    }

    @Override
    public void info(final Marker marker, final String format, final Object[] argArray) {
        logger_.info(marker, format, argArray);
    }

    @Override
    public void info(final Marker marker, final String msg, final Throwable t) {
        logger_.info(marker, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger_.isWarnEnabled();
    }

    @Override
    public void warn(final String msg) {
        logger_.warn(msg);
    }

    @Override
    public void warn(final String format, final Object arg) {
        logger_.warn(format, arg);
    }

    @Override
    public void warn(final String format, final Object[] argArray) {
        logger_.warn(format, argArray);
    }

    @Override
    public void warn(final String format, final Object arg1, final Object arg2) {
        logger_.warn(format, arg1, arg2);
    }

    @Override
    public void warn(final String msg, final Throwable t) {
        logger_.warn(msg, t);
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return logger_.isWarnEnabled(marker);
    }

    @Override
    public void warn(final Marker marker, final String msg) {
        logger_.warn(marker, msg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg) {
        logger_.warn(marker, format, arg);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
        logger_.warn(marker, format, arg1, arg2);
    }

    @Override
    public void warn(final Marker marker, final String format, final Object[] argArray) {
        logger_.warn(marker, format, argArray);
    }

    @Override
    public void warn(final Marker marker, final String msg, final Throwable t) {
        logger_.warn(marker, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger_.isErrorEnabled();
    }

    @Override
    public void error(final String msg) {
        logger_.error(msg);
    }

    @Override
    public void error(final String format, final Object arg) {
        logger_.error(format, arg);
    }

    @Override
    public void error(final String format, final Object arg1, final Object arg2) {
        logger_.error(format, arg1, arg2);
    }

    @Override
    public void error(final String format, final Object[] argArray) {
        logger_.error(format, argArray);
    }

    @Override
    public void error(final String msg, final Throwable t) {
        logger_.error(msg, t);
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return logger_.isErrorEnabled(marker);
    }

    @Override
    public void error(final Marker marker, final String msg) {
        logger_.error(marker, msg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg) {
        logger_.error(marker, format, arg);
    }

    @Override
    public void error(final Marker marker, final String format, final Object arg1, final Object arg2) {
        logger_.error(marker, format, arg1, arg2);
    }

    @Override
    public void error(final Marker marker, final String format, final Object[] argArray) {
        logger_.error(marker, format, argArray);
    }

    @Override
    public void error(final Marker marker, final String msg, final Throwable t) {
        logger_.error(marker, msg, t);
    }

}
