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

package jp.sourceforge.hotchpotch.coopie.groovy

import java.lang.annotation.Annotation

import org.t2framework.commons.meta.PropertyDesc

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvLayout
import jp.sourceforge.hotchpotch.coopie.csv.CsvElementInOut
import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.DefaultCsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.ElementReader
import jp.sourceforge.hotchpotch.coopie.csv.MapCsvLayout
import jp.sourceforge.hotchpotch.coopie.csv.QuoteMode
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader
import jp.sourceforge.hotchpotch.coopie.util.Annotations
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil
import jp.sourceforge.hotchpotch.coopie.util.LineSeparator
import jp.sourceforge.hotchpotch.coopie.util.PropertyAnnotationReader

class Csv {

    char elementSeparator = CsvSetting.COMMA
    char quoteMark = CsvSetting.DOUBLE_QUOTE
    String lineSeparator = CsvSetting.CRLF
    QuoteMode quoteMode = QuoteMode.ALWAYS_EXCEPT_NULL
    /*
     * param: String element
     * return: edited element
     */
    Closure elementEditor = { it }

    CsvReader openReader(input) {
        def setting = new DefaultCsvSetting(elementSeparator: elementSeparator, quoteMark: quoteMark, lineSeparator: lineSeparator, quoteMode: quoteMode)
        def io = new CsvElementInOut(setting)
        def reader = io.openReader(input)
        def CsvReader csvReader = new CsvReader(reader_: reader, elementEditor_: elementEditor)
        return csvReader
    }

    void eachRecord(input, Closure c) {
        openReader(input).eachRecord(c)
    }

    void eachRecordAsMap(input, Closure c) {
        def layout = new MapCsvLayout(elementSeparator: elementSeparator, quoteMark: quoteMark, lineSeparator: lineSeparator /*, quoteMode: quoteMode*/)
        def recordReader = layout.build().openReader(input)
        def csvReader = new CsvRecordReader(reader: recordReader)
        csvReader.eachRecord(c)
    }

    void eachRecordAsBean(input, beanClass, Closure c) {
        def BeanCsvLayout layout = BeanCsvLayout.getInstance(beanClass)
        layout.elementSeparator = elementSeparator
        layout.quoteMark = quoteMark
        layout.lineSeparator = elementSeparator
        layout.propertyAnnotationReader = new GroovyAnnotationReader()
        //layout.quoteMode = quoteMode;
        def recordReader = layout.build().openReader(input)
        def csvReader = new CsvRecordReader(reader: recordReader)
        csvReader.eachRecord(c)
    }

    void setLineSeparator(sep) {
        if (sep instanceof LineSeparator) {
            this.lineSeparator = ((LineSeparator)sep).separator
        } else {
            this.lineSeparator = sep
        }
    }

    static class CsvReader {
        private ElementReader reader_
        private Closure elementEditor_

        void eachRecord(Closure c) {
            try {
                def String[] record
                while ((record = reader_.readRecord()) != null) {
                    record = record.collect(elementEditor_)
                    c(record)
                }
            } finally {
                CloseableUtil.closeNoException(reader_)
            }
        }
    }

    static class CsvRecordReader {
        RecordReader reader

        void eachRecord(Closure c) {
            try {
                while (reader.hasNext()) {
                    def record = reader.read()
                    c(record)
                }
            } finally {
                CloseableUtil.closeNoException(reader)
            }
        }
    }

    static class GroovyAnnotationReader implements PropertyAnnotationReader {

        private PropertyAnnotationReader delegate_ = Annotations.getPropertyAnnotationReader()
        private PropertyAnnotationReader prop_ = new Annotations.FieldPropertyAnnotationReader()

        @Override
        public <ANN extends Annotation> ANN getAnnotation(
                PropertyDesc<?> propertyDesc, Class<ANN> annotationClass) {
            def ann = delegate_.getAnnotation(propertyDesc, annotationClass)
            if (ann != null) {
                return ann
            }
            return prop_.getAnnotation(propertyDesc, annotationClass)
        }
    }
}
