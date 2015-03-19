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

package jp.sourceforge.hotchpotch.coopie;


import org.hamcrest.core.SubstringMatcher;

public class CoopieMatchers {

    public static org.hamcrest.Matcher<java.lang.String> startsWithString(final String substring) {
        return new StringStartsWith(substring);
    }

    public static org.hamcrest.Matcher<java.lang.String> endsWithString(final String substring) {
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
