package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

public class MapCsvReader extends DefaultCsvReader<Map<String, String>> {

    public MapCsvReader() {
        this(new MapCsvLayout());
    }

    public MapCsvReader(final MapCsvLayout layout) {
        super(layout);
    }

}
