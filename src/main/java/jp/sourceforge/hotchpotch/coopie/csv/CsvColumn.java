package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface CsvColumn {

    /**
     * ヘッダ文字列
     */
    String label() default "";

    /**
     * 列位置
     * 空きは左へ詰めます
     */
    int order() default Integer.MAX_VALUE;

}
