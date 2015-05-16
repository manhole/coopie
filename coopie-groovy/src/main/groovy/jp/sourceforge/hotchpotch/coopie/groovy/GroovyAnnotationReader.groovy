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

package jp.sourceforge.hotchpotch.coopie.groovy

import java.lang.annotation.Annotation

import jp.sourceforge.hotchpotch.coopie.internal.PropertyDesc
import jp.sourceforge.hotchpotch.coopie.util.Annotations
import jp.sourceforge.hotchpotch.coopie.util.PropertyAnnotationReader

/**
 * @author manhole
 */
class GroovyAnnotationReader implements PropertyAnnotationReader {

    private PropertyAnnotationReader delegate_ = Annotations.getPropertyAnnotationReader()
    private PropertyAnnotationReader prop_ = new Annotations.FieldPropertyAnnotationReader()

    @Override
    public <ANN extends Annotation> ANN getAnnotation(
            PropertyDesc<?, ?> propertyDesc, Class<ANN> annotationClass) {
        def ann = delegate_.getAnnotation(propertyDesc, annotationClass)
        if (ann != null) {
            return ann
        }
        return prop_.getAnnotation(propertyDesc, annotationClass)
    }

}
