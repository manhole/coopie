package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;
import java.util.TreeMap;

public class MapRecordType<PROP> implements RecordType<Map<String, PROP>> {

    @Override
    public Map<String, PROP> newInstance() {
        return new TreeMap<String, PROP>();
    }

}
