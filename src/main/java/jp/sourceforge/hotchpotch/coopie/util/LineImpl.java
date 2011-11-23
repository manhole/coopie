package jp.sourceforge.hotchpotch.coopie.util;

public class LineImpl implements Line {

    private final String body_;
    private final int number_;
    private final LineSeparator separator_;

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

    @Override
    public int getNumber() {
        return number_;
    }

    @Override
    public LineSeparator getSeparator() {
        return separator_;
    }

}
