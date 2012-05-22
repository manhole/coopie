package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.csv.RecordDesc.OrderSpecified;
import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public abstract class AbstractMapCsvLayout<PROP> extends
        AbstractCsvLayout<Map<String, PROP>> {

    @Override
    protected AbstractCsvRecordDescSetup<Map<String, PROP>> getRecordDescSetup() {
        return new MapCsvRecordDescSetup<PROP>();
    }

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            /*
             * カラム名が設定されていない場合は、
             * Readの場合はヘッダから、
             * Writeの場合は1件目から、
             * カラム名を構築する。
             */
            setRecordDesc(new LazyMapRecordDesc<PROP>(this));
        }
    }

    protected static <PROP> ColumnDesc<Map<String, PROP>> newMapColumnDesc(
            final ColumnName columnName,
            final PropertyBinding<Map<String, PROP>, PROP> propertyBinding) {
        final MapColumnDesc<PROP> cd = new MapColumnDesc<PROP>();
        cd.setName(columnName);
        cd.setPropertyBinding(propertyBinding);
        return cd;
    }

    static class MapCsvRecordDescSetup<PROP> extends
            AbstractCsvRecordDescSetup<Map<String, PROP>> {

        private RecordDesc<Map<String, PROP>> recordDesc_;

        @Override
        public RecordDesc<Map<String, PROP>> getRecordDesc() {
            buildIfNeed();
            return recordDesc_;
        }

        private void buildIfNeed() {
            if (recordDesc_ != null) {
                return;
            }
            /*
             * 設定されているプロパティ名を対象に。
             */
            final ColumnDesc<Map<String, PROP>>[] cds = toColumnDescs(columnBuilders_);
            recordDesc_ = new DefaultRecordDesc<Map<String, PROP>>(cds,
                    OrderSpecified.SPECIFIED, new MapRecordType<PROP>());
        }

    }

    // TODO
    public static <PROP> ColumnDesc<Map<String, PROP>>[] toColumnDescs(
            final Collection<SimpleColumnBuilder> builders) {
        final ColumnDesc<Map<String, PROP>>[] cds = ColumnDescs
                .newColumnDescs(builders.size());
        int i = 0;
        for (final SimpleColumnBuilder builder : builders) {
            final ColumnName columnName = builder.getColumnName();
            final String propertyName = builder.getPropertyName();
            final PropertyBinding<Map<String, PROP>, PROP> propertyBinding = new MapPropertyBinding<PROP>(
                    propertyName);
            final ColumnDesc<Map<String, PROP>> cd = newMapColumnDesc(
                    columnName, propertyBinding);
            cds[i] = cd;
            i++;
        }
        return cds;
    }

    static class MapColumnDesc<PROP> implements ColumnDesc<Map<String, PROP>> {

        /**
         * CSV列名。
         */
        private ColumnName columnName_;
        private PropertyBinding<Map<String, PROP>, PROP> propertyBinding_;
        private Converter converter_;

        @Override
        public ColumnName getName() {
            return columnName_;
        }

        public void setName(final ColumnName name) {
            columnName_ = name;
        }

        public PropertyBinding<Map<String, PROP>, PROP> getPropertyBinding() {
            return propertyBinding_;
        }

        public void setPropertyBinding(
                final PropertyBinding<Map<String, PROP>, PROP> propertyBinding) {
            propertyBinding_ = propertyBinding;
        }

        public void setConverter(final Converter converter) {
            converter_ = converter;
        }

        @Override
        public String getValue(final Map<String, PROP> bean) {
            final PROP v = propertyBinding_.getValue(bean);
            return (String) v;
        }

        @Override
        public void setValue(final Map<String, PROP> bean, final String value) {
            // TODO PROP
            propertyBinding_.setValue(bean, (PROP) value);
        }
    }

    static class LazyMapRecordDesc<PROP> implements
            RecordDesc<Map<String, PROP>> {

        private static final Logger logger = LoggerFactory.getLogger();
        private final AbstractMapCsvLayout<PROP> layout_;

        public LazyMapRecordDesc(final AbstractMapCsvLayout<PROP> layout) {
            layout_ = layout;
        }

        /*
         * CSVを読むとき
         */
        @Override
        public RecordDesc<Map<String, PROP>> setupByHeader(final String[] header) {

            logger.debug("setupByHeader: {}", Arrays.toString(header));

            /*
             * ヘッダをMapのキーとして扱う。
             */
            /*
             * TODO これではCsvLayoutを毎回異なるインスタンスにしなければならない。
             * 一度設定すれば同一インスタンスのLayoutを使えるようにしたい。
             */
            layout_.setupColumns(new SetupBlock<CsvColumnSetup>() {
                @Override
                public void setup(final CsvColumnSetup setup) {
                    for (final String headerElem : header) {
                        setup.column(headerElem);
                    }
                }
            });

            final RecordDesc<Map<String, PROP>> built = layout_.getRecordDesc();
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
        public RecordDesc<Map<String, PROP>> setupByBean(
                final Map<String, PROP> bean) {
            /*
             * TODO これではCsvLayoutを毎回異なるインスタンスにしなければならない。
             * 一度設定すれば同一インスタンスのLayoutを使えるようにしたい。
             */
            layout_.setupColumns(new SetupBlock<CsvColumnSetup>() {
                @Override
                public void setup(final CsvColumnSetup setup) {
                    final Set<String> keys = bean.keySet();
                    for (final String key : keys) {
                        setup.column(key);
                    }
                }
            });

            final RecordDesc<Map<String, PROP>> built = layout_.getRecordDesc();
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
        public String[] getValues(final Map<String, PROP> bean) {
            throw new AssertionError();
        }

        @Override
        public void setValues(final Map<String, PROP> bean,
                final String[] values) {
            throw new AssertionError();
        }

        @Override
        public Map<String, PROP> newInstance() {
            throw new AssertionError();
        }

    }

}
