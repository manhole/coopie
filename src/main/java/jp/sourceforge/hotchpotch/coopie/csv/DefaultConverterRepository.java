package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.util.VarArgs.a;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.t2framework.commons.util.CollectionsUtil;

public class DefaultConverterRepository implements ConverterRepository {

    /*
     * キーは、Java側プロパティの型
     */
    private final Map<ConverterKey, Converter> propertyTypeMap_ = CollectionsUtil
            .newHashMap();

    /*
     * BEAN情報 + Property情報
     * - BEANの型 + Propertyの型
     * 
     * Property情報
     * - Propertyの型
     */

    @Override
    public Converter detect(final CsvColumnDef columnDef) {
        final Class<?> propertyType = columnDef.getPropertyType();
        final Converter converter = _detectByType(propertyType);
        return converter;
    }

    private Converter _detectByType(final Class<?> propertyType) {
        final Converter converter = propertyTypeMap_.get(new ConverterKey(
                propertyType, a(String.class)));
        return converter;
    }

    public void register(final Converter converter) {
        final Type[] genericInterfaces = converter.getClass()
                .getGenericInterfaces();
        for (final Type type : genericInterfaces) {
            final ParameterizedType pType = (ParameterizedType) type;
            final Type rawType = pType.getRawType();
            if (rawType instanceof Class) {
                if (Converter.class.isAssignableFrom((Class) rawType)) {
                    final Type[] actualTypeArguments = pType
                            .getActualTypeArguments();
                    // XXX 型チェックが必要かも
                    final Class propertyType = (Class) actualTypeArguments[0];
                    final Class outerType = (Class) actualTypeArguments[1];
                    propertyTypeMap_.put(new ConverterKey(propertyType,
                            a(outerType)), converter);
                    return;
                }
            }
        }
    }

    private static class ConverterKey {

        private final Class<?> propertyType_;
        private final Class<?>[] outerTypes_;
        private final int hashCode_;

        ConverterKey(final Class<?> propertyType, final Class<?>[] outerTypes) {
            propertyType_ = propertyType;
            outerTypes_ = outerTypes;

            final int coefficient = 37;
            int c = 17;
            c = c * coefficient + propertyType.hashCode();
            for (final Class<?> outerType : outerTypes) {
                c = c * coefficient + outerType.hashCode();
            }
            hashCode_ = c;
        }

        @Override
        public int hashCode() {
            return hashCode_;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ConverterKey)) {
                return false;
            }
            final ConverterKey another = (ConverterKey) obj;
            if (!propertyType_.equals(another.propertyType_)) {
                return false;
            }
            if (!Arrays.equals(outerTypes_, another.outerTypes_)) {
                return false;
            }
            return true;
        }

    }

}
