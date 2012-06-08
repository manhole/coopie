package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

class MapPropertyBinding<PROP> implements
        PropertyBinding<Map<String, PROP>, PROP> {

    private final String name_;

    public MapPropertyBinding(final String name) {
        name_ = name;
    }

    @Override
    public void setValue(final Map<String, PROP> bean, final PROP value) {
        bean.put(name_, value);
    }

    @Override
    public PROP getValue(final Map<String, PROP> bean) {
        final PROP v = bean.get(name_);
        return v;
    }

}
