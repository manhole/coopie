package jp.sourceforge.hotchpotch.coopie;

import org.junit.internal.matchers.SubstringMatcher;

public class CoopieMatchers {

    public static org.hamcrest.Matcher<java.lang.String> startsWithString(
            final String substring) {
        return new StringStartsWith(substring);
    }

    public static org.hamcrest.Matcher<java.lang.String> endsWithString(
            final String substring) {
        return new StringEndsWith(substring);
    }

    private static class StringStartsWith extends SubstringMatcher {

        protected StringStartsWith(final String substring) {
            super(substring);
        }

        @Override
        protected boolean evalSubstringOf(final String string) {
            return string.startsWith(substring);
        }

        @Override
        protected String relationship() {
            return "starts with";
        }

    }

    private static class StringEndsWith extends SubstringMatcher {

        protected StringEndsWith(final String substring) {
            super(substring);
        }

        @Override
        protected boolean evalSubstringOf(final String string) {
            return string.endsWith(substring);
        }

        @Override
        protected String relationship() {
            return "ends with";
        }

    }

}
