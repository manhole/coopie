package jp.sourceforge.hotchpotch.coopie.fl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface FixedLengthColumn {

    int beginIndex();

    int endIndex();

}
