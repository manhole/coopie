package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

class MapCsvReader extends DefaultCsvReader<Map<String, String>> {

    public MapCsvReader(final RecordDesc<Map<String, String>> layout) {
        super(layout);
    }

}
