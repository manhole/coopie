package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractCsvLayout.AbstractCsvRecordDescSetup.SimpleColumnBuilder;
import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public abstract class AbstractMapCsvLayout extends
        AbstractCsvLayout<Map<String, String>> {

    @Override
    protected AbstractCsvRecordDescSetup<Map<String, String>> getRecordDescSetup() {
        return new MapCsvRecordDescSetup();
    }

    protected RecordDesc<Map<String, String>> getRecordDesc() {
        if (recordDesc == null) {
            /*
             * カラム名が設定されていない場合は、
             * Readの場合はヘッダから、
             * Writeの場合は1件目から、
             * カラム名を構築する。
             */
            return new LazyMapRecordDesc(this);
        }
        return recordDesc;
    }

    protected static ColumnDesc<Map<String, String>> newMapColumnDesc(
            final ColumnName columnName,
            final PropertyBinding<Map<String, String>, String> propertyBinding) {
        final MapColumnDesc cd = new MapColumnDesc();
        cd.setName(columnName);
        cd.setPropertyBinding(propertyBinding);
        return cd;
    }

    static class MapCsvRecordDescSetup extends
            AbstractCsvRecordDescSetup<Map<String, String>> {

        @Override
        public RecordDesc<Map<String, String>> getRecordDesc() {
            /*
             * 設定されているプロパティ名を対象に。
             */
            final ColumnDesc<Map<String, String>>[] cds = toColumnDescs(columnBuilders);

            return new DefaultRecordDesc<Map<String, String>>(cds,
                    OrderSpecified.SPECIFIED, new MapRecordType());
        }

    }

    static ColumnDesc<Map<String, String>>[] toColumnDescs(
            final List<SimpleColumnBuilder> columns) {
        final ColumnDesc<Map<String, String>>[] cds = ColumnDescs
                .newColumnDescs(columns.size());
        int i = 0;
        for (final SimpleColumnBuilder builder : columns) {
            final ColumnName columnName = builder.getColumnName();
            final String propertyName = builder.getPropertyName();
            final PropertyBinding<Map<String, String>, String> propertyBinding = new MapPropertyBinding<String>(
                    propertyName);
            final ColumnDesc<Map<String, String>> cd = newMapColumnDesc(
                    columnName, propertyBinding);
            cds[i] = cd;
            i++;
        }
        return cds;
    }

    static class MapColumnDesc implements ColumnDesc<Map<String, String>> {

        /**
         * CSV列名。
         */
        private ColumnName name;

        private PropertyBinding<Map<String, String>, String> propertyBinding_;

        @Override
        public ColumnName getName() {
            return name;
        }

        public void setName(final ColumnName name) {
            this.name = name;
        }

        public PropertyBinding<Map<String, String>, String> getPropertyBinding() {
            return propertyBinding_;
        }

        public void setPropertyBinding(
                final PropertyBinding<Map<String, String>, String> propertyBinding) {
            propertyBinding_ = propertyBinding;
        }

        @Override
        public String getValue(final Map<String, String> bean) {
            return propertyBinding_.getValue(bean);
        }

        @Override
        public void setValue(final Map<String, String> bean, final String value) {
            propertyBinding_.setValue(bean, value);
        }

    }

    static class LazyMapRecordDesc implements RecordDesc<Map<String, String>> {

        private static final Logger logger = LoggerFactory.getLogger();
        private final AbstractMapCsvLayout layout;

        public LazyMapRecordDesc(final AbstractMapCsvLayout layout) {
            this.layout = layout;
        }

        /*
         * CSVを読むとき
         */
        @Override
        public RecordDesc<Map<String, String>> setupByHeader(
                final String[] header) {

            logger.debug("setupByHeader: {}", Arrays.toString(header));

            /*
             * ヘッダをMapのキーとして扱う。
             */
            /*
             * TODO これではCsvLayoutを毎回異なるインスタンスにしなければならない。
             * 一度設定すれば同一インスタンスのLayoutを使えるようにしたい。
             */
            layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
                @Override
                public void setup(final CsvColumnSetup setup) {
                    for (final String headerElem : header) {
                        setup.column(headerElem);
                    }
                }
            });

            final RecordDesc<Map<String, String>> built = layout
                    .getRecordDesc();
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
            layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
                @Override
                public void setup(final CsvColumnSetup setup) {
                    final Set<String> keys = bean.keySet();
                    for (final String key : keys) {
                        setup.column(key);
                    }
                }
            });

            final RecordDesc<Map<String, String>> built = layout
                    .getRecordDesc();
            if (built instanceof LazyMapRecordDesc) {
                // 意図しない無限ループを防ぐ
                throw new AssertionError();
            }
            return built;
        }

        @Override
        public String[] getHeaderValues() {
            throw new AssertionError();
        }

        @Override
        public OrderSpecified getOrderSpecified() {
            return OrderSpecified.NO;
        }

        @Override
        public String[] getValues(final Map<String, String> bean) {
            throw new AssertionError();
        }

        @Override
        public void setValues(final Map<String, String> bean,
                final String[] values) {
            throw new AssertionError();
        }

        @Override
        public Map<String, String> newInstance() {
            // これで良いのだっけ?
            throw new AssertionError();
        }

    }

}
