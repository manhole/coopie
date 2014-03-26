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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.t2framework.commons.util.ResourceUtil;

public class Sha1Test {

    @Test
    public void test1() throws Throwable {
        // ## Arrange ##
        final File file = ResourceUtil.getResourceAsFile(Sha1Test.class.getPackage().getName().replace('.', '/')
                + "/coopie-0.1.0-20100708.043157-1.pom");

        // ## Act ##
        final String actual = new Sha1().digest(file);

        // ## Assert ##
        assertEquals("e31ef5b931e16ce308cbad3b5c96360e1a1ea619", actual);
    }

}
