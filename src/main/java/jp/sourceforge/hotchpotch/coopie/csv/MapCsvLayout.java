package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;

public class MapCsvLayout extends AbstractCsvLayout<Map<String, String>> {

    @Override
    protected RecordDesc<Map<String, String>> buildRecordDesc() {
        if (columnNames != null) {
            final ColumnName[] names = columnNames.getColumnNames();
            final ColumnDesc<Map<String, String>>[] cds = newColumnDescs(names.length);
            int i = 0;
            for (final ColumnName columnName : names) {
                final ColumnDesc<Map<String, String>> cd = newMapColumnDesc(columnName);
                cds[i] = cd;
                i++;
            }
            return new DefaultRecordDesc<Map<String, String>>(cds,
                    OrderSpecified.SPECIFIED, withHeader);
        }

        /*
         * カラム名が設定されていない場合は、
         * Readの場合はヘッダから、
         * Writeの場合は1件目から、
         * カラム名を構築する。
         */
        return new LazyMapRecordDesc(this);
    }

    protected static ColumnDesc<Map<String, String>> newMapColumnDesc(
            final ColumnName columnName) {
        final MapColumnDesc cd = new MapColumnDesc();
        cd.setName(columnName);
        return cd;
    }

    @Override
    public CsvReader<Map<String, String>> openReader(final Reader reader) {
        final DefaultCsvReader<Map<String, String>> r = new DefaultCsvReader<Map<String, String>>(
                buildRecordDesc(), new MapRecordType());
        // TODO openで例外時にcloseすること
        r.open(reader);
        return r;
    }

    @Override
    public CsvWriter<Map<String, String>> openWriter(final Writer writer) {
        final DefaultCsvWriter<Map<String, String>> w = new DefaultCsvWriter<Map<String, String>>(
                buildRecordDesc());
        // TODO openで例外時にcloseすること
        w.open(writer);
        return w;
    }

    static class MapColumnDesc implements ColumnDesc<Map<String, String>> {

        /**
         * CSV列名。
         */
        private ColumnName name;

        @Override
        public ColumnName getName() {
            return name;
        }

        public void setName(final ColumnName name) {
            this.name = name;
        }

        @Override
        public String getValue(final Map<String, String> bean) {
            final String propertyName = name.getName();
            return bean.get(propertyName);
        }

        @Override
        public void setValue(final Map<String, String> bean, final String value) {
            final String propertyName = name.getName();
            bean.put(propertyName, value);
        }

    }

    static class LazyMapRecordDesc implements RecordDesc<Map<String, String>> {

        private final MapCsvLayout layout;

        public LazyMapRecordDesc(final MapCsvLayout layout) {
            this.layout = layout;
        }

        /*
         * CSVを読むとき
         */
        @Override
        public RecordDesc<Map<String, String>> setupByHeader(
                final String[] header) {

            /*
             * ヘッダをMapのキーとして扱う。
             */
            /*
             * TODO これではCsvLayoutを毎回異なるインスタンスにしなければならない。
             * 一度設定すれば同一インスタンスのLayoutを使えるようにしたい。
             */
            layout.setupColumns(new ColumnSetupBlock() {
                @Override
                public void setup(final ColumnSetup setup) {
                    int i = 0;
                    for (final String headerElem : header) {
                        setup.column(headerElem);
                        i++;
                    }
                }
            });

            final RecordDesc<Map<String, String>> built = layout
                    .buildRecordDesc();
            if (built instanceof LazyMapRecordDesc) {
                // 意図しない無限ループを防ぐ
                throw new AssertionError();
            }
            return built;
        }

        /*
         * CSVを書くとき
         */
        /*
         * 列名が設定されていない場合、
         * 1行目のMapのキーを、CSVのヘッダとする
         * (列名が設定されている場合は、そもそもこのクラスが使われない)
         */
        @Override
        public RecordDesc<Map<String, String>> setupByBean(
                final Map<String, String> bean) {
            /*
             * TODO これではCsvLayoutを毎回異なるインスタンスにしなければならない。
             * 一度設定すれば同一インスタンスのLayoutを使えるようにしたい。
             */
            layout.setupColumns(new ColumnSetupBlock() {
                @Override
                public void setup(final ColumnSetup setup) {
                    final Set<String> keys = bean.keySet();
                    for (final String key : keys) {
                        setup.column(key);
                    }
                }
            });

            final RecordDesc<Map<String, String>> built = layout
                    .buildRecordDesc();
            if (built instanceof LazyMapRecordDesc) {
                // 意図しない無限ループを防ぐ
                throw new AssertionError();
            }
            return built;
        }

        @Override
        public ColumnName[] getColumnNames() {
            throw new AssertionError();
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            return null;
        }

        @Override
        public String[] getValues(final Map<String, String> bean) {
            throw new AssertionError();
        }

        @Override
        public boolean isWithHeader() {
            return layout.withHeader;
        }

        @Override
        public void setValues(final Map<String, String> bean,
                final String[] values) {
            throw new AssertionError();
        }

    }

    static class MapRecordType implements RecordBeanType<Map<String, String>> {

        public MapRecordType() {
        }

        @Override
        public Map<String, String> newInstance() {
            return new TreeMap<String, String>();
        }

    }

}
