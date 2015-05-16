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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author manhole
 */
public class CodePointsTest {

    @Test
    public void onechar() throws Throwable {
        // ## Arrange ##
        final CodePoints cp = CodePoints.create("a");

        // ## Act ##
        // ## Assert ##
        assertThat("a".codePointAt(0), is(97));

        assertThat(cp.size(), is(1));
        assertThat(cp.getAt(0), is(97));
    }

    @Test
    public void twochars() throws Throwable {
        // ## Arrange ##
        final CodePoints cp = CodePoints.create("aA");
        assertThat("a".codePointAt(0), is(97));
        assertThat("A".codePointAt(0), is(65));

        // ## Act ##
        // ## Assert ##

        assertThat(cp.size(), is(2));
        assertThat(cp.getAt(0), is(97));
        assertThat(cp.getAt(1), is(65));
    }

    @Test
    public void surrogate() throws Throwable {
        // ## Arrange ##
        final CodePoints cp = CodePoints.create("𠮷野家");
        assertThat("𠮷".codePointAt(0), is(134071));
        assertThat("野".codePointAt(0), is(37326));
        assertThat("家".codePointAt(0), is(23478));

        // ## Act ##
        // ## Assert ##

        assertThat(cp.size(), is(3));
        assertThat(cp.getAt(0), is(134071));
        assertThat(cp.getAt(1), is(37326));
        assertThat(cp.getAt(2), is(23478));
    }

}
