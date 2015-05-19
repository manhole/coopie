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

package jp.sourceforge.hotchpotch.coopie.spreadsheet;

import jp.sourceforge.hotchpotch.coopie.csv.AbstractBeanCsvLayout;
import jp.sourceforge.hotchpotch.coopie.csv.RecordReader;
import jp.sourceforge.hotchpotch.coopie.csv.RecordWriter;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class BeanExcelLayout<BEAN> extends AbstractBeanCsvLayout<BEAN> {

    private DefaultExcelWriter.WriteEditor writeEditor_;

    public BeanExcelLayout(final Class<BEAN> beanClass) {
        super(beanClass);
    }

    public RecordReader<BEAN> openSheetReader(final Sheet sheet) {
        return build().openSheetReader(sheet);
    }

    public RecordWriter<BEAN> openSheetWriter(final Workbook workbook, final Sheet sheet) {
        return build().openSheetWriter(workbook, sheet);
    }

    public void setWriteEditor(final DefaultExcelWriter.WriteEditor writeEditor) {
        writeEditor_ = writeEditor;
    }

    public ExcelRecordInOut<BEAN> build() {
        prepareBuild();

        final ExcelRecordInOut<BEAN> obj = new ExcelRecordInOut<BEAN>();
        obj.setRecordDesc(getRecordDesc());
        obj.setWithHeader(isWithHeader());
        obj.setElementReaderHandler(getElementReaderHandler());
        obj.setWriteEditor(writeEditor_);
        return obj;
    }

}
