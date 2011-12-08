package jp.sourceforge.hotchpotch.coopie.util;

public class LineImpl implements Line {

    private String body_;
    private int number_;
    private LineSeparator separator_;

    public LineImpl() {
    }

    public LineImpl(final String body, final int number,
            final LineSeparator separator) {
        body_ = body;
        number_ = number;
        separator_ = separator;
    }

    @Override
    public String getBody() {
        return body_;
    }

    public void setBody(final String body) {
        body_ = body;
    }

    @Override
    public int getNumber() {
        return number_;
    }

    public void setNumber(final int number) {
        number_ = number;
    }

    @Override
    public LineSeparator getSeparator() {
        return separator_;
    }

    public void setSeparator(final LineSeparator separator) {
        separator_ = separator;
    }

    @Override
    public Line reinit(final String body, final int number,
            final LineSeparator separator) {
        setBody(body);
        setNumber(number);
        setSeparator(separator);
        return this;
    }

}
