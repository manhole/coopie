package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

public class MapPropertyBinding<PROP> implements
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

    public static class Factory implements
            PropertyBindingFactory<Map<String, Object>> {

        private static Factory INSTANCE = new Factory();

        public static Factory getInstance() {
            return INSTANCE;
        }

        @Override
        public <PROP> PropertyBinding<Map<String, Object>, PROP> getPropertyBinding(
                final String name) {
            final MapPropertyBinding<PROP> pb = new MapPropertyBinding<PROP>(
                    name);
            return (PropertyBinding) pb;
        }

    }

}
