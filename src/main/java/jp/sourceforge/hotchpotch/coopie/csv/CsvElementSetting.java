package jp.sourceforge.hotchpotch.coopie.csv;

public class CsvElementSetting implements ElementSetting {

    private final CsvSetting csvSetting_;

    public CsvElementSetting(final CsvSetting csvSetting) {
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
        writer.setQuoteMode(QuoteMode.ALWAYS_EXCEPT_NULL);
        return writer;
    }

    protected Rfc4180Reader createReader() {
        final Rfc4180Reader reader = new Rfc4180Reader();
        reader.setElementSeparator(csvSetting_.getElementSeparator());
        reader.setQuoteMark(csvSetting_.getQuoteMark());
        return reader;
    }

}
