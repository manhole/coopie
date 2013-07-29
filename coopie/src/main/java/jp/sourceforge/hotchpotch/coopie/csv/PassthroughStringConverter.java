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

package jp.sourceforge.hotchpotch.coopie.csv;

public class PassthroughStringConverter implements Converter<String, String> {

    private static PassthroughStringConverter INSTANCE = new PassthroughStringConverter();

    public static PassthroughStringConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public String convertTo(final String from) {
        return from;
    }

    @Override
    public String convertFrom(final String from) {
        return from;
    }

}