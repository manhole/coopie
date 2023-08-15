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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.CoopieException;

public class ToStringFormat {

    private static final String NULL = "<null>";
    private static final String EMPTY = "<empty>";

    private static final ThreadLocal<Context> threadLocal = new ThreadLocal<Context>() {
        @Override
        protected Context initialValue() {
            return new Context();
        };
    };

    private final FieldDescComparator comparator_ = new FieldDescComparator();
    private int maxDepth_ = 1;

    public ToStringFormat() {
    }

    public <T> String format(final T obj) {
        final StringBuilder sb = new StringBuilder();
        final Context context = threadLocal.get();
        try {
            append(sb, obj, context);
            return new String(sb);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private <T> void append(final Appendable out, final T obj, final Context context) throws IOException {

        if (obj == null) {
            out.append(NULL);
            return;
        }
        if ("".equals(obj)) {
            out.append(EMPTY);
            return;
        }

        // 入れ子になっている
        if (context.contains(obj)) {
            out.append("<..>");
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
                appendString(out, (String) obj);
            } else if (obj instanceof Float) {
                out.append(obj.toString());
            } else if (obj instanceof Integer) {
                out.append(obj.toString());
            } else if (obj instanceof Long) {
                out.append(obj.toString());
            } else if (obj instanceof Boolean) {
                out.append(obj.toString());
            } else if (obj instanceof java.sql.Date) {
                out.append(obj.toString());
            } else if (obj instanceof java.sql.Time) {
                out.append(obj.toString());
            } else if (obj instanceof Class) {
                final String s = Class.class.cast(obj).getName();
                out.append("Class[");
                out.append(s);
                out.append("]");
            } else if (obj instanceof java.util.Date) {
                final String s = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS").format(obj);
                out.append(s);
            } else if (clazz.isArray()) {
                appendObjectArray(out, (Object[]) obj, context);
            } else {
                appendObject(out, obj, context);
            }
        } finally {
            context.leave(obj);
        }
    }

    private <T> void appendObject(final Appendable out, final T obj, final Context context) throws IOException {

        @SuppressWarnings("unchecked")
        final Class<T> clazz = (Class<T>) obj.getClass();
        final String simpleName = clazz.getSimpleName();

        if (context.isDepthOver(maxDepth_)) {
            final String s = identityHashCode(obj);
            out.append(simpleName);
            out.append("@");
            out.append(s);
            return;
        }

        final List<FieldDesc<?>> descs = getFieldDescs(clazz);

        // classがpublic classでなくても続行

        Collections.sort(descs, comparator_);

        boolean first = true;
        out.append(simpleName);
        out.append("[");
        for (final FieldDesc<?> fd : descs) {
            if (first) {
                first = false;
            } else {
                out.append(", ");
            }
            out.append(fd.getName());
            out.append("=");
            final Object value = fd.getValue(obj);
            append(out, value, context);
        }
        out.append("]");
    }

    private void appendObjectArray(final Appendable out, final Object[] array, final Context context)
            throws IOException {
        boolean first = true;
        out.append("[");
        for (final Object obj : array) {
            if (first) {
                first = false;
            } else {
                out.append(", ");
            }
            append(out, obj, context);
        }
        out.append("]");
    }

    private void appendString(final Appendable out, final String str) throws IOException {
        final char[] chars = str.toCharArray();
        for (final char c : chars) {
            switch (c) {
            case 0x09:
                out.append("<TAB>");
                break;
            case 0x0a:
                out.append("<LF>");
                break;
            case 0x0d:
                out.append("<CR>");
                break;

            default:
                out.append(c);
                break;
            }
        }
    }

    private List<FieldDesc<?>> getFieldDescs(final Class<?> clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(declaredFields, true);
        final ArrayList<FieldDesc<?>> l = new ArrayList<>();
        for (final Field field : declaredFields) {
            final FieldDesc<?> fd = new FieldDesc<>(field);
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
        maxDepth_ = depth;
    }

    private static class FieldDescComparator implements Comparator<FieldDesc<?>> {

        @Override
        public int compare(final FieldDesc<?> o1, final FieldDesc<?> o2) {
            final String name1 = o1.getName();
            final String name2 = o2.getName();
            return name1.compareTo(name2);
        }

    }

    private static class FieldDesc<T> {

        private final Field field_;

        FieldDesc(final Field field) {
            field_ = field;
        }

        public String getName() {
            return field_.getName();
        }

        @SuppressWarnings("unchecked")
        public T getValue(final Object instance) {
            try {
                final Object object = field_.get(instance);
                return (T) object;
            } catch (final IllegalAccessException e) {
                throw new CoopieException(e);
            }
        }

        @Override
        public String toString() {
            return getName();
        }

    }

    private static class Context {

        private final Set<Object> set_ = new IdentityHashSet<>();
        private int depth_;

        public boolean contains(final Object obj) {
            return set_.contains(obj);
        }

        public boolean isDepthOver(final int maxDepth) {
            if (maxDepth < depth_) {
                return true;
            }
            return false;
        }

        public void enter(final Object obj) {
            depth_++;
            set_.add(obj);
        }

        public void leave(final Object obj) {
            set_.remove(obj);
            depth_--;
        }

    }

}
