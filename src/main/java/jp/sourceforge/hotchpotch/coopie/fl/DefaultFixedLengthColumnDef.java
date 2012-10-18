package jp.sourceforge.hotchpotch.coopie.fl;

import jp.sourceforge.hotchpotch.coopie.csv.Converter;
import jp.sourceforge.hotchpotch.coopie.csv.PassthroughStringConverter;

class DefaultFixedLengthColumnDef implements FixedLengthColumnDef {

    private String propertyName_;
    private int beginIndex_;
    private int endIndex_;
    private Converter<?, ?> converter_ = PassthroughStringConverter
            .getInstance();

    @Override
    public String getPropertyName() {
        return propertyName_;
    }

    public void setPropertyName(final String propertyName) {
        propertyName_ = propertyName;
    }

    @Override
    public int getBeginIndex() {
        return beginIndex_;
    }

    public void setBeginIndex(final int beginIndex) {
        beginIndex_ = beginIndex;
    }

    @Override
    public int getEndIndex() {
        return endIndex_;
    }

    public void setEndIndex(final int endIndex) {
        endIndex_ = endIndex;
    }

    @Override
    public Converter<?, ?> getConverter() {
        return converter_;
    }

    @Override
    public void setConverter(final Converter<?, ?> converter) {
        converter_ = converter;
    }

}
