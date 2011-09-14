package jp.sourceforge.hotchpotch.coopie.csv;

public class NullConverter implements Converter<String, String> {

    private static final NullConverter INSTANCE = new NullConverter();

    public static Converter<String, String> getInstance() {
        return INSTANCE;
    }

    @Override
    public void convertTo(final Converter.ObjectRepresentation<String> from,
            final Converter.ExternalRepresentation<String> to) {
        final String o = from.get();
        to.add(o);
    }

    @Override
    public void convertFrom(
            final Converter.ExternalRepresentation<String> from,
            final Converter.ObjectRepresentation<String> to) {
        final String o = from.get();
        to.add(o);
    }

}
