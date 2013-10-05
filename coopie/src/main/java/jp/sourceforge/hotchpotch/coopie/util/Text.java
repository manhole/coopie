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
        final LineReadable reader = new LineReader(new StringReader(s));
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

    public Line getLine(final int lineNo) {
        final Line[] ls = getLines();
        final Line l = ls[lineNo];
        return l;
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

    public Text convertLineSeparator(final LineSeparator lineSeparator) {
        final String s = convertLineSeparator0(rawText_, lineSeparator);
        return instantiate(s);
    }

    private static String convertLineSeparator0(final String s,
            final LineSeparator lineSeparator) {
        final LineReadable reader = new LineReader(new StringReader(s));
        final StringBuilder sb = new StringBuilder();
        try {
            for (final Line line : reader) {
                sb.append(line.getBody());
                if (line.getSeparator() != LineSeparator.NONE) {
                    sb.append(lineSeparator.getSeparator());
                }
            }
        } finally {
            CloseableUtil.closeNoException(reader);
        }
        return sb.toString();
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

    public Text compactSpace() {
        boolean occur = false;
        final StringBuilder sb = new StringBuilder();
        final char[] chars = rawText_.toCharArray();
        for (final char ch : chars) {
            if (WHITESPACE.isTrim(ch)) {
                if (!occur) {
                    occur = true;
                }
            } else {
                if (occur) {
                    occur = false;
                    sb.append(' ');
                }
                sb.append(ch);
            }
        }
        if (occur) {
            occur = false;
            sb.append(' ');
        }
        final String s = sb.toString();
        return instantiate(s);
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
            return Character.isWhitespace(c) || c == 0xA0;
        }
    };

}
