package jp.sourceforge.hotchpotch.coopie.csv;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.junit.Test;
import org.t2framework.commons.util.CollectionsUtil;

public class MapExcelWriterTest {

    /**
     * カラム順を設定できること。
     */
    @Test
    public void write2() throws Throwable {
        // ## Arrange ##
        final MapExcelLayout layout = new MapExcelLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                setup.column("aaa");
                setup.column("ccc");
                setup.column("bbb");
            }
        });

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(baos);

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        bean.put("aaa", "あ1");
        bean.put("bbb", "い1");
        bean.put("ccc", "う1");
        csvWriter.write(bean);

        bean.put("aaa", "あ2");
        bean.put("bbb", "い2");
        bean.put("ccc", "う2");
        csvWriter.write(bean);

        bean.put("aaa", "あ3");
        bean.put("bbb", "い3");
        bean.put("ccc", "う3");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        BeanExcelWriterTest.assertWrite2(baos);
    }

    /**
     * CSVヘッダが無い場合。
     */
    @Test
    public void write_noheader() throws Throwable {
        // ## Arrange ##
        final MapExcelLayout layout = new MapExcelLayout();
        layout.setupColumns(new SetupBlock<CsvColumnSetup>() {
            @Override
            public void setup(final CsvColumnSetup setup) {
                /*
                 * プロパティ名, CSV項目名 の順
                 */
                setup.column("ccc");
                setup.column("aaa");
                setup.column("bbb");
            }
        });
        layout.setWithHeader(false);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // ## Act ##
        final RecordWriter<Map<String, String>> csvWriter = layout
                .openWriter(baos);

        final Map<String, String> bean = CollectionsUtil.newHashMap();
        bean.put("aaa", "あ1");
        bean.put("bbb", "い1");
        bean.put("ccc", "う1");
        csvWriter.write(bean);

        bean.put("aaa", "あ2");
        bean.put("bbb", "い2");
        bean.put("ccc", "う2");
        csvWriter.write(bean);

        csvWriter.close();

        // ## Assert ##
        BeanExcelWriterTest.assertWriteNoheader(baos);
    }

}
