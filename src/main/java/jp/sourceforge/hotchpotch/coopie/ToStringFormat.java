package jp.sourceforge.hotchpotch.coopie;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
import org.t2framework.commons.meta.MethodDesc;
import org.t2framework.commons.meta.PropertyDesc;

public class ToStringFormat {

    private static final String NULL = "<null>";

    private static final ThreadLocal<Set<Object>> threadLocal = new ThreadLocal<Set<Object>>() {
        @Override
        protected Set<Object> initialValue() {
            return new IdentityHashSet<Object>();
        };
    };

    private final PropertyDescComparator comparator = new PropertyDescComparator();

    public ToStringFormat() {
    }

    public <T> String format(final T obj) {
        final StringBuilder sb = new StringBuilder();
        final Set<Object> set = threadLocal.get();
        append(sb, obj, set);
        return new String(sb);
    }

    private <T> void append(final StringBuilder sb, final T obj,
            final Set<Object> set) {
        if (obj == null) {
            sb.append(NULL);
            return;
        }

        // 入れ子になっている
        if (set.contains(obj)) {
            sb.append("<..>");
            return;
        }

        /*
         * value.toStringの中でToStringFormatが使われる場合を想定して、
         * ThreadLocalにインスタンスを出力したかどうかを保持する。
         */
        set.add(obj);
        try {
            if (obj instanceof String) {
                sb.append((String) obj);
            } else if (obj instanceof Float) {
                sb.append(obj);
            } else if (obj instanceof Integer) {
                sb.append(obj);
            } else if (obj instanceof Long) {
                sb.append(obj);
            } else if (obj instanceof Boolean) {
                sb.append(obj);
            } else if (obj instanceof java.sql.Date) {
                sb.append(obj);
            } else if (obj instanceof java.sql.Time) {
                sb.append(obj);
            } else if (obj instanceof java.util.Date) {
                final String s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
                        .format(obj);
                sb.append(s);
            } else {
                appendObject(sb, obj, set);
            }
        } finally {
            set.remove(obj);
        }
    }

    private <T> void appendObject(final StringBuilder sb, final T obj,
            final Set<Object> set) {

        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final BeanDesc<T> beanDesc = BeanDescFactory.getBeanDesc(clazz);

        // classがpublic classでなくても続行

        final List<PropertyDesc<T>> allPropertyDesc = beanDesc
                .getAllPropertyDesc();
        final List<PropertyDesc<T>> descs = new ArrayList<PropertyDesc<T>>();
        for (final PropertyDesc<T> pd : allPropertyDesc) {
            if (pd.isReadable()) {
                final MethodDesc methodDesc = pd.getReadMethodDesc();
                if (!methodDesc.isPublic()) {
                    throw new IllegalStateException("method [" + methodDesc
                            + "] is not public");
                }
                final Method method = methodDesc.getMethod();
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                descs.add(pd);
            }
        }
        Collections.sort(descs, comparator);

        boolean first = true;
        sb.append(clazz.getSimpleName());
        sb.append("[");
        for (final PropertyDesc<T> pd : descs) {
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
    }

    private static class PropertyDescComparator implements
            Comparator<PropertyDesc<?>> {

        @Override
        public int compare(final PropertyDesc<?> o1, final PropertyDesc<?> o2) {
            final String name1 = o1.getPropertyName();
            final String name2 = o2.getPropertyName();
            return name1.compareTo(name2);
        }

    }

}
