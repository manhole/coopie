package jp.sourceforge.hotchpotch.coopie.csv;

public interface Converter<OBJ, EXT> {

    void convertTo(ObjectRepresentation<OBJ> from,
            ExternalRepresentation<EXT> to);

    void convertFrom(ExternalRepresentation<EXT> from,
            ObjectRepresentation<OBJ> to);

    interface Representation<T> {

        void add(T s);

        T get();

    }

    interface ObjectRepresentation<T> extends Representation<T> {
    }

    interface ExternalRepresentation<T> extends Representation<T> {
    }

}
