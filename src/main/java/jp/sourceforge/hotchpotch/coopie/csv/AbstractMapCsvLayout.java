package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Arrays;
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
            final MapPropertyBinding.Factory pbf = MapPropertyBinding.Factory
                    .getInstance();
            final ColumnDesc<Map<String, PROP>>[] cds = (ColumnDesc[]) AbstractBeanCsvLayout
                    .toColumnDescs(getColumnBuilders(), pbf);
            recordDesc_ = new DefaultRecordDesc<Map<String, PROP>>(cds,
                    OrderSpecified.SPECIFIED, new MapRecordType<PROP>());
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
