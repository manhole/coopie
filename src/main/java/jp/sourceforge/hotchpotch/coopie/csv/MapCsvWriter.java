package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;
import java.util.Set;

class MapCsvWriter extends DefaultCsvWriter<Map<String, String>> {

    public MapCsvWriter(final MapCsvLayout layout) {
        super(layout);
    }

    @Override
    protected void writeHeader(final Map<String, String> bean) {
        if (csvLayout.getNames() == null) {
            /*
             * 列名が設定されていない場合、
             * 1行目のMapのキーを、CSVのヘッダとする
             */
            csvLayout.setupColumns(new ColumnSetupBlock() {
                @Override
                public void setup(final ColumnSetup setup) {
                    final Set<String> keys = bean.keySet();
                    for (final String key : keys) {
                        setup.column(key);
                    }
                }
            });
        }

        super.writeHeader(bean);
    }

}
