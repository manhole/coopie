package jp.sourceforge.hotchpotch.coopie.logging;

import java.util.Collections;
import java.util.List;

import org.t2framework.commons.util.CollectionsUtil;

public class SimpleLog implements Log {

    private StringBuilder format_;
    private List<Object> argList_;

    public SimpleLog(final String format, final String[] args) {

        if (format != null) {
            format_ = new StringBuilder();
            format_.append(format);
        }
        if (args != null) {
            argList_ = CollectionsUtil.newArrayList();
            Collections.addAll(argList_, args);
        }
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

}
