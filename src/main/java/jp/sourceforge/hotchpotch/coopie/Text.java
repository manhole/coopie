package jp.sourceforge.hotchpotch.coopie;

import org.t2framework.commons.util.StringUtil;

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

    public String[] getLines() {
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
                final String trimWhitespace = Text.trimWhitespace(line);
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

    public static String trimWhitespace(final String s) {
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

        return s.substring(begin, end);
    }

    @Override
    public String toString() {
        return getRawText();
    }

}
