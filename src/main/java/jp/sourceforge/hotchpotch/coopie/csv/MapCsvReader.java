package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;
import java.util.TreeMap;

class MapCsvReader extends DefaultCsvReader<Map<String, String>> {

    public MapCsvReader(final RecordDesc<Map<String, String>> layout) {
        super(layout);
    }

    @Override
    protected Map<String, String> newInstance() {
        return new TreeMap<String, String>();
    }

}
