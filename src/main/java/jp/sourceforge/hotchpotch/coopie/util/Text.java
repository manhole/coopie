package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.t2framework.commons.exception.IORuntimeException;
import org.t2framework.commons.util.CollectionsUtil;

/*
 * immutable
 */
public class Text {

    private final String rawText_;
    private Line[] lines_;
    private String concated_;

    public Text(final String text) {
        rawText_ = text;
    }

    public String getRawText() {
        return rawText_;
    }

    private Line[] getLines() {
        if (lines_ == null) {
            lines_ = toLines(rawText_);
        }
        return lines_;
    }

    private Line[] toLines(final String s) {
        final List<Line> l = CollectionsUtil.newArrayList();
        final LineReader reader = new LineReadable(new StringReader(s));
        try {
            while (true) {
                final Line line = reader.readLine();
                if (line == null) {
                    break;
                }
                l.add(line);
            }
            final Line[] a = l.toArray(new Line[l.size()]);
            return a;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            CloseableUtil.closeNoException(reader);
        }
    }

    public int getLineSize() {
        final Line[] ls = getLines();
        return ls.length;
    }

    public Text getLineAsText(final int lineNo) {
        final Line[] ls = getLines();
        final Line l = ls[lineNo];
        return instantiate(l.getBody());
    }

    public boolean containsText(final String s) {
        if (concated_ == null) {
            final StringBuilder sb = new StringBuilder();
            final Line[] lines = getLines();
            for (final Line line : lines) {
                final String trimWhitespace = trimWhitespace0(line.getBody());
                sb.append(trimWhitespace);
            }
            concated_ = sb.toString();
        }
        final String trim = s.trim();
        return concated_.contains(trim);
    }

    public boolean containsLine(final String s) {
        final Line[] lines = getLines();
        for (final Line line : lines) {
            if (line.getBody().contains(s)) {
                return true;
            }
        }
        return false;
    }

    public Text trim(final TrimStrategy trimStrategy) {
        final String trim = trim0(rawText_, trimStrategy);
        return instantiate(trim);
    }

    public Text trimWhitespace() {
        final String trim = trimWhitespace0(rawText_);
        return instantiate(trim);
    }

    public static String trim(final String s, final TrimStrategy trimStrategy) {
        return trim0(s, trimStrategy);
    }

    public static String trimWhitespace(final String s) {
        return trim0(s, WHITESPACE);
    }

    private String trimWhitespace0(final String s) {
        return trim0(s, WHITESPACE);
    }

    private static String trim0(final String s, final TrimStrategy trimStrategy) {

        final int len = s.length();
        int begin = 0;
        for (int i = 0; i < len; i++) {
            final char c = s.charAt(i);
            if (trimStrategy.isTrim(c)) {
                begin++;
            } else {
                break;
            }
        }

        int end = len;
        for (int i = len - 1; begin < i; i--) {
            final char c = s.charAt(i);
            if (trimStrategy.isTrim(c)) {
                end--;
            } else {
                break;
            }
        }

        final String substring = s.substring(begin, end);
        return substring;
    }

    @Override
    public String toString() {
        return getRawText();
    }

    public Text deleteChar(final char del) {
        final char[] chars = rawText_.toCharArray();
        final StringBuilder sb = new StringBuilder();
        for (final char c : chars) {
            if (c == del) {
            } else {
                sb.append(c);
            }
        }
        return instantiate(sb.toString());
    }

    protected Text instantiate(final String s) {
        return new Text(s);
    }

    public static int length(final CharSequence cs) {
        if (cs == null) {
            return 0;
        }
        final String str = cs.toString();
        final int count = length(str);
        return count;
    }

    public static int length(final String str) {
        if (str == null) {
            return 0;
        }
        final int length = str.length();
        final int count = str.codePointCount(0, length);
        return count;
    }

    public static String substring(final String str, final int beginIndex,
            final int endIndex) {
        final int actualBegin = str.offsetByCodePoints(0, beginIndex);
        final int actualEnd = str.offsetByCodePoints(0, endIndex);
        final String s = str.substring(actualBegin, actualEnd);
        return s;
    }

    public interface TrimStrategy {

        boolean isTrim(char c);

    }

    public static TrimStrategy STANDARD = new TrimStrategy() {
        @Override
        public boolean isTrim(final char c) {
            // java.lang.Stringと同じ
            return c <= ' ';
        }
    };

    public static TrimStrategy WHITESPACE = new TrimStrategy() {
        @Override
        public boolean isTrim(final char c) {
            return Character.isWhitespace(c);
        }
    };

}
