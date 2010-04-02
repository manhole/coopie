package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Writer;
import java.util.Map;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.Closable;
import au.com.bytecode.opencsv.CSVWriter;

public class MapCsvWriter extends AbstractCsvWriter<Map<String, String>>
    implements Closable {

    private final MapColumnLayout columnLayout;

    private boolean writtenHeader;

    public MapCsvWriter() {
        columnLayout = new MapColumnLayout();
    }

    public MapCsvWriter(final MapColumnLayout columnLayout) {
        this.columnLayout = columnLayout;
    }

    @Override
    public void open(final Writer writer) {
        final CsvSetting setting = getCsvSetting();
        csvWriter = new CSVWriter(writer, setting.getElementSeparator(),
            setting.getQuoteMark(), setting.getLineSeparator());
        closed = false;
    }

    @Override
    public void write(final Map<String, String> bean) {
        if (!writtenHeader) {
            if (columnLayout.getNames() == null) {
                /*
                 * 列名が設定されていない場合、
                 * 1行目のMapのキーを、CSVのヘッダとする
                 */
                columnLayout.setupColumns(new ColumnSetup() {
                    @Override
                    public void setup() {
                        // TODO Auto-generated method stub
                        final Set<String> keys = bean.keySet();
                        for (final String key : keys) {
                            column(key);
                        }
                    }
                });
            }

            writeHeader();
            writtenHeader = true;
        }
        super.write(bean);
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
