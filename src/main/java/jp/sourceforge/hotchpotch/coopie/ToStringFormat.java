package jp.sourceforge.hotchpotch.coopie;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.t2framework.commons.exception.IllegalAccessRuntimeException;
import org.t2framework.commons.meta.BeanDesc;
import org.t2framework.commons.meta.BeanDescFactory;

public class ToStringFormat {

    private static final String NULL = "<null>";

    private static final ThreadLocal<Context> threadLocal = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        };
    };

    private final FieldDescComparator comparator = new FieldDescComparator();

    public ToStringFormat() {
    }

    private int maxDepth = 1;

    public <T> String format(final T obj) {
        final StringBuilder sb = new StringBuilder();
        final Context context = threadLocal.get();
        append(sb, obj, context);
        return new String(sb);
    }

    private <T> void append(final StringBuilder sb, final T obj,
            final Context context) {
        if (obj == null) {
            sb.append(NULL);
            return;
        }

        // 入れ子になっている
        if (context.contains(obj)) {
            sb.append("<..>");
            return;
        }

        /*
         * value.toStringの中でToStringFormatが使われる場合を想定して、
         * ThreadLocalにインスタンスを出力したかどうかを保持する。
         */
        context.enter(obj);
        try {
            @SuppressWarnings("unchecked")
            final Class<T> clazz = (Class<T>) obj.getClass();
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
            } else if (obj instanceof Class) {
                final String s = Class.class.cast(obj).getName();
                sb.append("Class[");
                sb.append(s);
                sb.append("]");
            } else if (obj instanceof java.util.Date) {
                final String s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS")
                        .format(obj);
                sb.append(s);
            } else if (clazz.isArray()) {
                appendObjectArray(sb, (Object[]) obj, context);
            } else {
                appendObject(sb, obj, context);
            }
        } finally {
            context.leave(obj);
        }
    }

    private <T> void appendObject(final StringBuilder sb, final T obj,
            final Context context) {

        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final String simpleName = clazz.getSimpleName();

        if (context.isDepthOver(maxDepth)) {
            final String s = identityHashCode(obj);
            sb.append(simpleName);
            sb.append("@");
            sb.append(s);
            return;
        }

        final BeanDesc<T> beanDesc = BeanDescFactory.getBeanDesc(clazz);
        final List<FieldDesc<?>> descs = getFieldDescs(clazz);

        // classがpublic classでなくても続行

        Collections.sort(descs, comparator);

        boolean first = true;
        sb.append(simpleName);
        sb.append("[");
        for (final FieldDesc<?> fd : descs) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(fd.getName());
            sb.append("=");
            final Object value = fd.getValue(obj);
            append(sb, value, context);
        }
        sb.append("]");
    }

    private void appendObjectArray(final StringBuilder sb,
            final Object[] array, final Context context) {
        boolean first = true;
        sb.append("[");
        for (final Object obj : array) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            append(sb, obj, context);
        }
        sb.append("]");
    }

    private List<FieldDesc<?>> getFieldDescs(final Class<?> clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(declaredFields, true);
        final ArrayList<FieldDesc<?>> l = new ArrayList<FieldDesc<?>>();
        for (final Field field : declaredFields) {
            final FieldDesc<?> fd = new FieldDesc<Object>(clazz, field);
            l.add(fd);
        }
        return l;
    }

    private String identityHashCode(final Object obj) {
        final int code = System.identityHashCode(obj);
        final String s = Integer.toHexString(code);
        return s;
    }

    public void setMaxDepth(final int depth) {
        maxDepth = depth;
    }

    private static class FieldDescComparator implements
            Comparator<FieldDesc<?>> {

        @Override
        public int compare(final FieldDesc<?> o1, final FieldDesc<?> o2) {
            final String name1 = o1.getName();
            final String name2 = o2.getName();
            return name1.compareTo(name2);
        }

    }

    private static class FieldDesc<T> {

        private final Class<?> clazz;
        private final Field field;

        FieldDesc(final Class<?> clazz, final Field field) {
            this.clazz = clazz;
            this.field = field;
        }

        public String getName() {
            return field.getName();
        }

        @SuppressWarnings("unchecked")
        public T getValue(final Object instance) {
            try {
                final Object object = field.get(instance);
                return (T) object;
            } catch (final IllegalAccessException e) {
                throw new IllegalAccessRuntimeException(clazz, e);
            }
        }

        @Override
        public String toString() {
            return getName();
        }

    }

    private static class Context {

        private final Set<Object> set = new IdentityHashSet<Object>();
        private int depth;

        public boolean contains(final Object obj) {
            return set.contains(obj);
        }

        public boolean isDepthOver(final int maxDepth) {
            if (maxDepth < depth) {
                return true;
            }
            return false;
        }

        public void enter(final Object obj) {
            depth++;
            set.add(obj);
        }

        public void leave(final Object obj) {
            set.remove(obj);
            depth--;
        }

    }

}
