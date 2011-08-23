package jp.sourceforge.hotchpotch.coopie;

import org.t2framework.commons.util.StringUtil;

/*
 * immutable
 */
public class Text {

    private final String rawText;
    private String[] lines;
    private String concated;

    public Text(final String text) {
        rawText = text;
    }

    public String getRawText() {
        return rawText;
    }

    private String[] getLines() {
        if (lines == null) {
            lines = StringUtil.split(rawText, "\r\n");
        }
        return lines;
    }

    public String getLine(final int lineNo) {
        return getLines()[lineNo];
    }

    public boolean containsText(final String s) {
        if (concated == null) {
            final StringBuilder sb = new StringBuilder();
            final String[] lines = getLines();
            for (final String line : lines) {
                final String trimWhitespace = trimWhitespace0(line);
                sb.append(trimWhitespace);
            }
            concated = sb.toString();
        }
        final String trim = s.trim();
        return concated.contains(trim);
    }

    public boolean containsLine(final String s) {
        final String[] lines = getLines();
        for (final String line : lines) {
            if (line.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public Text trimWhitespace() {
        final String trim = trimWhitespace0(rawText);
        return instantiate(trim);
    }

    private String trimWhitespace0(final String s) {
        final int len = s.length();
        int begin = 0;
        for (int i = 0; i < len; i++) {
            final char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
                begin++;
            } else {
                break;
            }
        }

        int end = len;
        for (int i = len - 1; begin < i; i--) {
            final char c = s.charAt(i);
            if (Character.isWhitespace(c)) {
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
        final char[] chars = rawText.toCharArray();
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

}
