package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.t2framework.commons.meta.PropertyDesc;

class Annotations {

    static <ANN extends Annotation> ANN getAnnotation(
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
