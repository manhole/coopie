package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;
import java.util.TreeMap;

class MapRecordType implements RecordType<Map<String, String>> {

    @Override
    public Map<String, String> newInstance() {
        return new TreeMap<String, String>();
    }

}
