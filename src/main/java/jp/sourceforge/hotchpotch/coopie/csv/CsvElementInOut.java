package jp.sourceforge.hotchpotch.coopie.csv;

public class CsvElementInOut implements ElementInOut {

    private final CsvSetting csvSetting_;
    private LineReaderHandler lineReaderHandler_;

    public CsvElementInOut(final CsvSetting csvSetting) {
        if (csvSetting == null) {
            throw new NullPointerException("csvSetting");
        }
        csvSetting_ = csvSetting;
    }

    @Override
    public ElementWriter openWriter(final Appendable appendable) {
        final Rfc4180Writer writer = createWriter();
        writer.open(appendable);
        return writer;
    }

    @Override
    public ElementReader openReader(final Readable readable) {
        final Rfc4180Reader reader = createReader();
        reader.open(readable);
        return reader;
    }

    protected Rfc4180Writer createWriter() {
        final Rfc4180Writer writer = new Rfc4180Writer();
        writer.setElementSeparator(csvSetting_.getElementSeparator());
        writer.setLineSeparator(csvSetting_.getLineSeparator());
        writer.setQuoteMark(csvSetting_.getQuoteMark());
        writer.setQuoteMode(csvSetting_.getQuoteMode());
        return writer;
    }

    protected Rfc4180Reader createReader() {
        final Rfc4180Reader reader = new Rfc4180Reader();
        reader.setElementSeparator(csvSetting_.getElementSeparator());
        reader.setQuoteMark(csvSetting_.getQuoteMark());
        if (lineReaderHandler_ != null) {
            reader.setLineReaderHandler(lineReaderHandler_);
        }
        return reader;
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

}
