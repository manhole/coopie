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
