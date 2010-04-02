package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Map;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.Closable;

public class MapCsvWriter extends AbstractCsvWriter<Map<String, String>>
    implements Closable {

    private final MapColumnLayout columnLayout;

    public MapCsvWriter() {
        columnLayout = new MapColumnLayout();
    }

    public MapCsvWriter(final MapColumnLayout columnLayout) {
        this.columnLayout = columnLayout;
    }

    @Override
    protected void writeHeader(final Map<String, String> bean) {
        if (columnLayout.getNames() == null) {
            /*
             * 列名が設定されていない場合、
             * 1行目のMapのキーを、CSVのヘッダとする
             */
            columnLayout.setupColumns(new ColumnSetup() {
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

    @Override
    protected String[] getValues(final Map<String, String> bean) {
        return columnLayout.getValues(bean);
    }

    @Override
    protected ColumnName[] getColumnNames() {
        return columnLayout.getNames();
    }

}
