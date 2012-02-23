package jp.sourceforge.hotchpotch.coopie.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BinaryStreamOperation {

    private static final int K = 1024;
    private static final int DEFAULT_BUFF_SIZE = K * 8;

    private int bufferSize_ = DEFAULT_BUFF_SIZE;

    public void pipe(final InputStream is, final OutputStream os)
            throws IOException {
        final byte[] buf = new byte[bufferSize_];
        for (int len = 0; (len = is.read(buf, 0, buf.length)) != -1;) {
            os.write(buf, 0, len);
        }
    }

    public void setBufferSize(final int bufferSize) {
        bufferSize_ = bufferSize;
    }

}
