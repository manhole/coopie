package jp.sourceforge.hotchpotch.coopie;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.t2framework.commons.exception.FileNotFoundRuntimeException;
import org.t2framework.commons.exception.IORuntimeException;

public class FileOperation {

    private static final int DEFAULT_BUFF_SIZE = 1024 * 8;
    // FileOPeration
    private String prefix = "fop";
    private String suffix = ".tmp";
    private String encoding = "UTF-8";
    private int bufferSize = DEFAULT_BUFF_SIZE;

    public File createTempFile() {
        final File f = createTempFile(prefix);
        return f;
    }

    public File createTempFile(final String p) {
        try {
            final File f = File.createTempFile(p, suffix);
            return f;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public File createTempFile(final File parent) {
        try {
            final File f = File.createTempFile(prefix, suffix, parent);
            return f;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public File createTempDir() {
        final File f = createTempFile();
        f.delete();
        f.mkdir();
        return f;
    }

    public void delete(final File file) {
        // TODO Auto-generated method stub

    }

    public void write(final File file, final String text) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(fos, encoding);
                writer.append(text);
            } finally {
                closeNoException(writer);
            }
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(fos);
        }
    }

    public String read(final File file) {
        final byte[] bytes = readAsBytes(file);
        try {
            final String s = new String(bytes, encoding);
            return s;
        } catch (final UnsupportedEncodingException e) {
            throw new IORuntimeException(e);
        }
    }

    public byte[] readAsBytes(final File file) {
        final BufferedInputStream is = openBufferedInputStream(file);
        try {
            return readAsBytes(is);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(is);
        }
    }

    private byte[] readAsBytes(final BufferedInputStream is) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pipe(is, baos);
        final byte[] bytes = baos.toByteArray();
        return bytes;
    }

    private void pipe(final InputStream is, final OutputStream os)
        throws IOException {
        final byte[] buf = new byte[bufferSize];
        for (int len = 0; (len = is.read(buf, 0, buf.length)) != -1;) {
            os.write(buf, 0, len);
        }
    }

    private BufferedInputStream openBufferedInputStream(final File file) {
        final FileInputStream fis = openInputStream(file);
        final BufferedInputStream bis = new BufferedInputStream(fis, bufferSize);
        return bis;
    }

    private FileInputStream openInputStream(final File file) {
        try {
            final FileInputStream fis = new FileInputStream(file);
            return fis;
        } catch (final FileNotFoundException e) {
            throw new FileNotFoundRuntimeException(e, file);
        }
    }

    private void closeNoException(final Closeable closeable) {
        IOUtil.closeNoException(closeable);
    }

    public void setBufferSize(final int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setSuffix(final String suffix) {
        this.suffix = suffix;
    }

    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }

}
