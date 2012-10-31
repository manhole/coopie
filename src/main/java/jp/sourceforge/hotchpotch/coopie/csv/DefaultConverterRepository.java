package jp.sourceforge.hotchpotch.coopie.csv;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.t2framework.commons.util.CollectionsUtil;

public class DefaultConverterRepository implements ConverterRepository {

    private final Map<Class, Converter> propertyTypeMap_ = CollectionsUtil
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
        final Converter converter = propertyTypeMap_.get(propertyType);
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
                    final Type propertyType = actualTypeArguments[0];
                    final Type outerType = actualTypeArguments[1];
                    propertyTypeMap_.put((Class) propertyType, converter);
                    return;
                }
            }
        }
    }

}
