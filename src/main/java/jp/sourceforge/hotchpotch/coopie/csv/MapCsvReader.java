package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;

public class MapCsvReader extends AbstractCsvReader<Map<String, String>> {

    public MapCsvReader() {
        csvLayout = new MapCsvLayout();
    }

    public MapCsvReader(final MapCsvLayout columnLayout) {
        csvLayout = columnLayout;
    }

}
