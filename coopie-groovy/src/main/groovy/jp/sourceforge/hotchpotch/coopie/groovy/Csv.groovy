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

import jp.sourceforge.hotchpotch.coopie.csv.BeanCsvLayout
import jp.sourceforge.hotchpotch.coopie.csv.ConverterRepository
import jp.sourceforge.hotchpotch.coopie.csv.CsvElementInOut
import jp.sourceforge.hotchpotch.coopie.csv.CsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.DefaultCsvSetting
import jp.sourceforge.hotchpotch.coopie.csv.ElementReader
import jp.sourceforge.hotchpotch.coopie.csv.ElementWriter
import jp.sourceforge.hotchpotch.coopie.csv.MapCsvLayout
import jp.sourceforge.hotchpotch.coopie.csv.QuoteMode
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter
import jp.sourceforge.hotchpotch.coopie.util.Annotations
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil
import jp.sourceforge.hotchpotch.coopie.util.LineSeparator
import jp.sourceforge.hotchpotch.coopie.util.PropertyAnnotationReader

import org.t2framework.commons.meta.PropertyDesc

class Csv {

    char elementSeparator = CsvSetting.COMMA
    char quoteMark = CsvSetting.DOUBLE_QUOTE
    String lineSeparator = CsvSetting.CRLF
    QuoteMode quoteMode = QuoteMode.ALWAYS_EXCEPT_NULL
    ConverterRepository converterRepository

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
        BeanCsvLayout layout = createBeanCsvLayout(beanClass)
        def recordReader = layout.build().openReader(input)
        def csvReader = new CsvRecordReader(reader: recordReader)
        csvReader.eachRecord(c)
    }

    void withWriter(output, Closure c) {
        def setting = new DefaultCsvSetting(elementSeparator: elementSeparator, quoteMark: quoteMark, lineSeparator: lineSeparator, quoteMode: quoteMode)
        def io = new CsvElementInOut(setting)
        def writer = io.openWriter(output)
        try {
            def csvWriter = new CsvWriter(writer:writer)
            c(csvWriter)
        } finally {
            CloseableUtil.closeNoException(writer)
        }
    }

    void withBeanWriter(output, beanClass, Closure c) {
        BeanCsvLayout layout = createBeanCsvLayout(beanClass)
        def recordWriter = layout.build().openWriter(output)
        try {
            def csvWriter = new CsvRecordWriter(writer: recordWriter)
            c(csvWriter)
        } finally {
            CloseableUtil.closeNoException(recordWriter)
        }
    }

    void setLineSeparator(sep) {
        if (sep instanceof LineSeparator) {
            this.lineSeparator = ((LineSeparator)sep).separator
        } else {
            this.lineSeparator = sep
        }
    }

    BeanCsvLayout createBeanCsvLayout(beanClass) {
        def BeanCsvLayout layout = BeanCsvLayout.getInstance(beanClass)
        layout.elementSeparator = elementSeparator
        layout.lineSeparator = lineSeparator
        layout.quoteMark = quoteMark
        layout.quoteMode = quoteMode
        if (converterRepository) {
            layout.converterRepository = converterRepository
        }
        layout.propertyAnnotationReader = new GroovyAnnotationReader()
        layout
    }

    static class CsvReader {
        private ElementReader reader_
        private Closure elementEditor_

        void eachRecord(Closure c) {
            int paramCount = c.getMaximumNumberOfParameters()

            if (paramCount == 1) {
                if (CsvRecord.isAssignableFrom(c.parameterTypes[0])) {
                    _eachRecordWithRecord(c)
                } else {
                    _eachRecordWithArray(c)
                }
            } else {
                _eachRecordWithList(c, (0..paramCount-1))
            }
        }

        private void _eachRecordWithArray(Closure c) {
            try {
                def String[] record
                while ((record = reader_.readRecord()) != null) {
                    record = record.collect(elementEditor_)
                    c.call(record)
                }
            } finally {
                CloseableUtil.closeNoException(reader_)
            }
        }

        private void _eachRecordWithRecord(Closure c) {
            try {
                def r = new CsvRecord()
                int index = -1;
                def String[] record
                while ((record = reader_.readRecord()) != null) {
                    index++
                    record = record.collect(elementEditor_)
                    r.elements = record
                    r.index = index
                    c.call(r)
                }
            } finally {
                CloseableUtil.closeNoException(reader_)
            }
        }
        private void _eachRecordWithList(Closure c, Range range) {
            try {
                def String[] record
                while ((record = reader_.readRecord()) != null) {
                    record = record.collect(elementEditor_)
                    def args = []
                    range.step(1) { i ->
                        if (i < record.length) {
                            args << record[i]
                        } else {
                            args << null
                        }
                    }
                    c.call(args)
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

    static class CsvWriter {
        ElementWriter writer

        CsvWriter leftShift(List line) {
            def arr = new String[line.size()]
            line.toArray(arr)
            writer.writeRecord(arr)
            this
        }
    }

    static class CsvRecordWriter {
        RecordWriter writer
        CsvRecordWriter leftShift(record) {
            writer.write(record)
            this
        }
    }

    static class CsvRecord {
        String[] elements
        int index
        String getAt(int index) {
            return elements[index]
        }
        int length() {
            return elements.length
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
