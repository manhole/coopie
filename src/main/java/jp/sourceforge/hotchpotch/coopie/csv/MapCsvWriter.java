package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;
import java.util.Set;

public class MapCsvWriter extends AbstractCsvWriter<Map<String, String>> {

    public MapCsvWriter() {
        csvLayout = new MapCsvLayout();
    }

    public MapCsvWriter(final MapCsvLayout columnLayout) {
        this.csvLayout = columnLayout;
    }

    @Override
    protected void writeHeader(final Map<String, String> bean) {
        if (csvLayout.getNames() == null) {
            /*
             * 列名が設定されていない場合、
             * 1行目のMapのキーを、CSVのヘッダとする
             */
            csvLayout.setupColumns(new ColumnSetup() {
                @Override
                public void setup() {
                    final Set<String> keys = bean.keySet();
                    for (final String key : keys) {
                        column(key);
                    }
                }
            });
        }

        super.writeHeader(bean);
    }

}
