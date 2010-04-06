package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

class MapCsvReader extends DefaultCsvReader<Map<String, String>> {

    public MapCsvReader(final MapCsvLayout layout) {
        super(layout);
    }

}
