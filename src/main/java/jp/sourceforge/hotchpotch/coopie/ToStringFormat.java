package jp.sourceforge.hotchpotch.coopie;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.PropertyDesc;

public class ToStringFormat {

    private static final ThreadLocal<Set<Object>> threadLocal = new ThreadLocal<Set<Object>>() {
        @Override
        protected Set<Object> initialValue() {
            return new IdentityHashSet<Object>();
        };
    };

    public ToStringFormat() {
    }

    @SuppressWarnings("unchecked")
    public String format(final Object obj) {
        final StringBuilder sb = new StringBuilder();
        final Class<? extends Object> clazz = obj.getClass();
        final BeanDesc<?> beanDesc = BeanDescFactory.getBeanDesc(clazz);
        final List descs = beanDesc.getAllPropertyDesc();
        boolean first = true;
        final Set<Object> set = threadLocal.get();
        sb.append(clazz.getSimpleName());
        sb.append("[");

        for (final Iterator<PropertyDesc> it = descs.iterator(); it.hasNext();) {
            final PropertyDesc pd = it.next();
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(pd.getPropertyName());
            sb.append("=");
            final Object value = pd.getValue(obj);
            append(sb, value, set);
        }

        sb.append("]");
        return new String(sb);
    }

    private void append(final StringBuilder sb, final Object value,
        final Set<Object> set) {
        if (value == null) {
            sb.append("<null>");
            return;
        }

        // 入れ子になっている
        if (set.contains(value)) {
            sb.append("<..>");
            return;
        }

        set.add(value);
        try {
            sb.append(value);
        } finally {
            set.remove(value);
        }
    }

}
