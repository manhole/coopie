package jp.sourceforge.hotchpotch.coopie.csv;

public interface PropertyBinding<BEAN, PROP> {

    void setValue(BEAN bean, PROP value);

    PROP getValue(BEAN bean);

}
