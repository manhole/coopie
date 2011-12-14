package jp.sourceforge.hotchpotch.coopie.fl;

import java.io.IOException;

import jp.sourceforge.hotchpotch.coopie.csv.ElementParserContext;
import jp.sourceforge.hotchpotch.coopie.csv.ElementReader;
import jp.sourceforge.hotchpotch.coopie.csv.LineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.csv.PassThroughLineReaderHandler;
import jp.sourceforge.hotchpotch.coopie.util.CloseableUtil;
import jp.sourceforge.hotchpotch.coopie.util.ClosingGuardian;
import jp.sourceforge.hotchpotch.coopie.util.Line;
import jp.sourceforge.hotchpotch.coopie.util.LineReadable;

import org.t2framework.commons.exception.IORuntimeException;

public class FixedLengthReader implements ElementReader {

    private boolean closed_ = true;
    @SuppressWarnings("unused")
    private final Object finalizerGuardian_ = new ClosingGuardian(this);

    private LineReadable reader_;
    private final FixedLengthElementDesc[] elementDescs_;
    private int lineNo_;
    private LineReaderHandler lineReaderHandler_ = PassThroughLineReaderHandler
            .getInstance();

    private final ElementParserContext parserContext_ = FixedLengthParserContext
            .getInstance();

    public FixedLengthReader(final FixedLengthElementDesc[] columns) {
        elementDescs_ = columns;
    }

    public void open(final Readable readable) {
        reader_ = new LineReadable(readable);
        closed_ = false;
    }

    @Override
    public int getRecordNumber() {
        return lineNo_;
    }

    @Override
    public int getLineNumber() {
        return lineNo_;
    }

    @Override
    public String[] readRecord() {
        try {
            final Line line = readLine();
            if (line == null) {
                return null;
            }

            final String body = line.getBody();
            final String[] record = new String[elementDescs_.length];
            for (int i = 0; i < elementDescs_.length; i++) {
                final FixedLengthElementDesc elementDesc = elementDescs_[i];
                final String elem = elementDesc.read(body);
                record[i] = elem;
            }
            lineNo_++;
            return record;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    protected Line readLine() throws IOException {
        while (true) {
            final Line line = lineReaderHandler_.readLine(reader_);
            if (line == null) {
                return null;
            }
            if (!lineReaderHandler_.acceptLine(line, parserContext_)) {
                continue;
            }
            return line;
        }
    }

    @Override
    public boolean isClosed() {
        return closed_;
    }

    @Override
    public void close() throws IOException {
        closed_ = true;
        CloseableUtil.closeNoException(reader_);
    }

    public void setLineReaderHandler(final LineReaderHandler lineReaderHandler) {
        lineReaderHandler_ = lineReaderHandler;
    }

    private static class FixedLengthParserContext implements
            ElementParserContext {

        private static final ElementParserContext INSTANCE = new FixedLengthParserContext();

        public static ElementParserContext getInstance() {
            return INSTANCE;
        }

        /**
         * 固定長ファイルでは、行をまたがる要素はあり得ないため、
         * 常にfalseを返す。
         */
        @Override
        public boolean isInElement() {
            return false;
        }

    }

}
