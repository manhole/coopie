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

import java.io.StringReader;

import org.junit.Test;

public class CharacterStreamOperationTest {

    @Test
    public void pipe() throws Throwable {
        // ## Arrange ##
        final Readable in = new StringReader("0123456789abcdefghijklmnopqrstuvwxyz");
        final Appendable out = new StringBuilder();

        // ## Act ##
        final CharacterStreamOperation chars = new CharacterStreamOperation();
        chars.pipe(in, out);

        // ## Assert ##
        assertThat(out.toString(), is("0123456789abcdefghijklmnopqrstuvwxyz"));
    }

}
