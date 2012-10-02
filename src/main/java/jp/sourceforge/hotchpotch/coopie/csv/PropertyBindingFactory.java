package jp.sourceforge.hotchpotch.coopie.csv;

public interface PropertyBindingFactory<BEAN> {

    <PROP> PropertyBinding<BEAN, PROP> getPropertyBinding(String name);

}
