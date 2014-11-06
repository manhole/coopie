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

package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public class ClosingGuardian {

    private static final Logger logger = LoggerFactory.getLogger();

    static final String STACKTRACE_TEXT = "warning by ClosingGuardian";
    static final String SUCCESS_TEXT = "ok, already closed";

    private final Closable closable_;
    private final String createdBy_;

    public ClosingGuardian(final Closable closable) {
        if (closable == null) {
            throw new NullPointerException("closable");
        }
        closable_ = closable;

        /*
         * where construct instance
         */
        if (logger.isDebugEnabled()) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            new UnclosedWarning(STACKTRACE_TEXT).printStackTrace(pw);
            createdBy_ = sw.toString().trim();
        } else {
            createdBy_ = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (closable_.isClosed()) {
                logger.debug(SUCCESS_TEXT + ": {}", closable_.getClass().getName());
                return;
            }
            warn();
            try {
                closable_.close();
            } catch (final IOException e) {
                logger.warn("closing failure at finalize", e);
            }
        } finally {
            super.finalize();
        }
    }

    protected void warn() {
        if (logger.isWarnEnabled()) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            pw.print("closed at finalize: " + closable_.getClass().getName() + ", " + closable_);
            if (createdBy_ != null) {
                pw.println();
                pw.print(createdBy_);
            }
            logger.warn(sw.toString());
        }
    }

    private static class UnclosedWarning extends Exception {

        private static final long serialVersionUID = 1L;

        UnclosedWarning(final String message) {
            super(message);
        }

    }

}
