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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.t2framework.commons.meta.PropertyDesc;

public class Annotations {

    public static <ANN extends Annotation> ANN getAnnotation(
            final PropertyDesc<?> propertyDesc, final Class<ANN> annotationClass) {
        if (propertyDesc.isReadable()) {
            final Method reader = propertyDesc.getReadMethod();
            final ANN annotation = reader.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        if (propertyDesc.isWritable()) {
            final Method writer = propertyDesc.getWriteMethod();
            final ANN annotation = writer.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        return null;
    }

}
