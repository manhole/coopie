/*
 * Copyright 2010 manhole
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.csv;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;

public abstract class AbstractMapCsvLayout<PROP> extends AbstractCsvLayout<Map<String, PROP>> {

    protected void prepareOpen() {
        if (getRecordDesc() == null) {
            final CsvRecordDef recordDef = getRecordDef();
            if (recordDef != null) {
                final RecordDesc<Map<String, PROP>> recordDesc = createRecordDesc(recordDef);
                setRecordDesc(recordDesc);
            } else {
                /*
                 * カラム名が設定されていない場合は、
                 * Readの場合はヘッダから、
                 * Writeの場合は1件目から、
                 * カラム名を構築する。
                 */
                setRecordDesc(new LazyMapRecordDesc<PROP>(this));
            }
        }

        if (getRecordDesc() == null) {
            throw new AssertionError("recordDesc");
        }
    }

    @Override
    protected PropertyBindingFactory<Map<String, PROP>> createPropertyBindingFactory() {
        return MapPropertyBinding.Factory.getInstance();
    }

    @Override
    protected RecordType<Map<String, PROP>> createRecordType() {
        return new MapRecordType<PROP>();
    }

    static class LazyMapRecordDesc<PROP> implements RecordDesc<Map<String, PROP>> {

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

            // TODO 素直にRecordDescを取得したい
            layout_.prepareOpen();

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
        public RecordDesc<Map<String, PROP>> setupByBean(final Map<String, PROP> bean) {
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

            // TODO 素直にRecordDescを取得したい
            layout_.prepareOpen();

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
        public void setValues(final Map<String, PROP> bean, final String[] values) {
            throw new AssertionError();
        }

        @Override
        public Map<String, PROP> newInstance() {
            throw new AssertionError();
        }

    }

}
