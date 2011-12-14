package jp.sourceforge.hotchpotch.coopie.logging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

public class SimpleLog implements Log {

    private StringBuilder format_;
    private List<Object> argList_;

    public SimpleLog() {
    }

    public SimpleLog(final String format, final Object[] args) {
        append(format, args);
    }

    @Override
    public String getFormat() {
        if (format_ == null) {
            return null;
        }
        return format_.toString();
    }

    @Override
    public Object[] getArgs() {
        if (argList_ == null) {
            return null;
        }
        return argList_.toArray(new Object[argList_.size()]);
    }

    public void append(final String format, final Object... args) {
        appendFormat(format);
        appendArgs(args);
    }

    public void appendFormat(final String format) {
        if (format != null) {
            initFormatIfNeed();
            format_.append(format);
        }
    }

    private void appendArgs(final Object... args) {
        if (args != null) {
            initArgsIfNeed();
            Collections.addAll(argList_, args);
        }
    }

    private void initFormatIfNeed() {
        if (format_ == null) {
            format_ = new StringBuilder();
        }
    }

    private void initArgsIfNeed() {
        if (argList_ == null) {
            argList_ = new ArrayList<Object>();
            argList_ = CollectionsUtil.newArrayList();
        }
    }

}
