package jp.sourceforge.hotchpotch.coopie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;
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

    @SuppressWarnings("unchecked")
    public <T> String format(final T obj) {
        if (obj == null) {
            return NULL;
        }
        final StringBuilder sb = new StringBuilder();
        final Class<T> clazz = (Class<T>) obj.getClass();
        final BeanDesc<T> beanDesc = BeanDescFactory.getBeanDesc(clazz);
        final List<PropertyDesc<T>> descs = new ArrayList<PropertyDesc<T>>(
                beanDesc.getAllPropertyDesc());
        Collections.sort(descs, comparator);

        boolean first = true;
        final Set<Object> set = threadLocal.get();
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
        return new String(sb);
    }

    private void append(final StringBuilder sb, final Object value,
            final Set<Object> set) {
        if (value == null) {
            sb.append(NULL);
            return;
        }

        // 入れ子になっている
        if (set.contains(value)) {
            sb.append("<..>");
            return;
        }

        /*
         * value.toStringの中でToStringFormatが使われる場合を想定して、
         * ThreadLocalにインスタンスを出力したかどうかを保持する。
         */
        set.add(value);
        try {
            sb.append(value);
        } finally {
            set.remove(value);
        }
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
