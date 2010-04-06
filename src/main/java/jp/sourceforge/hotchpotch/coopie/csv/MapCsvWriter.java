package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

class MapCsvWriter extends DefaultCsvWriter<Map<String, String>> {

    public MapCsvWriter(final RecordDesc<Map<String, String>> layout) {
        super(layout);
    }

}
