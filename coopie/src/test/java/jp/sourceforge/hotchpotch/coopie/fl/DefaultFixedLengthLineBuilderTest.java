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

package jp.sourceforge.hotchpotch.coopie.fl;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * @author manhole
 */
public class DefaultFixedLengthLineBuilderTest {

    @Test
    public void test1() throws Throwable {
        // ## Arrange ##
        final DefaultFixedLengthLineBuilder builder = new DefaultFixedLengthLineBuilder();

        // ## Act ##
        builder.write("abc", 0, 3);

        // ## Assert ##

        assertThat(builder.buildString(), is("abc"));
    }

    @Test
    public void test2() throws Throwable {
        // ## Arrange ##
        final DefaultFixedLengthLineBuilder builder = new DefaultFixedLengthLineBuilder();

        // ## Act ##
        builder.write("abc", 2, 5);

        // ## Assert ##
        assertThat(builder.buildString(), is("  abc"));
    }

    @Test
    public void test3() throws Throwable {
        // ## Arrange ##
        final DefaultFixedLengthLineBuilder builder = new DefaultFixedLengthLineBuilder();

        // ## Act ##
        builder.write("abc", 2, 6);

        // ## Assert ##
        assertThat(builder.buildString(), is("  abc "));
    }

}
