package jp.sourceforge.hotchpotch.coopie.csv;

public class PassthroughStringConverter implements Converter {

    private static PassthroughStringConverter INSTANCE = new PassthroughStringConverter();

    public static PassthroughStringConverter getInstance() {
        return INSTANCE;
    }

    @Override
    public void convertTo(final Object[] from, final String[] to) {
        to[0] = (String) from[0];
    }

    @Override
    public void convertFrom(final String[] from, final Object[] to) {
        to[0] = from[0];
    }

}
