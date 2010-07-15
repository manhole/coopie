package jp.sourceforge.hotchpotch.coopie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.t2framework.commons.exception.FileNotFoundRuntimeException;
import org.t2framework.commons.exception.IORuntimeException;

public class FileOperation {

    private static final int DEFAULT_BUFF_SIZE = 1024 * 8;
    // FileOPeration
    private String prefix = "fop";
    private String suffix = ".tmp";
    private String encoding = "UTF-8";
    private int bufferSize = DEFAULT_BUFF_SIZE;
    private final FileWalker deleteWalker = new DeleteWalker();

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

    public File createTempDir(final File parent) {
        final File f = createTempFile(parent);
        f.delete();
        f.mkdir();
        return f;
    }

    public File createFile(final File parent, final String name) {
        final File file = new File(parent, name);
        try {
            file.createNewFile();
            return file;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public File createDirectory(final File parent, final String name) {
        final File file = new File(parent, name);
        file.mkdir();
        return file;
    }

    public void delete(final File file) {
        walk(file, deleteWalker);
    }

    public void write(final File file, final String text) {
        final Writer writer = openBufferedWriter(file);
        try {
            writer.append(text);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(writer);
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

    private BufferedWriter openBufferedWriter(final File file) {
        final OutputStreamWriter osw = openOutputStreamWriter(file);
        final BufferedWriter writer = new BufferedWriter(osw, bufferSize);
        return writer;
    }

    private OutputStreamWriter openOutputStreamWriter(final File file) {
        final FileOutputStream fos = openOutputStream(file);
        try {
            final OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
            return osw;
        } catch (final UnsupportedEncodingException e) {
            closeNoException(fos);
            throw new IORuntimeException(e);
        }
    }

    private FileOutputStream openOutputStream(final File file) {
        try {
            final FileOutputStream fos = new FileOutputStream(file);
            return fos;
        } catch (final FileNotFoundException e) {
            throw new FileNotFoundRuntimeException(e, file);
        }
    }

    private BufferedOutputStream openBufferedOutputStream(final File file) {
        final FileOutputStream fos = openOutputStream(file);
        final BufferedOutputStream bos = new BufferedOutputStream(fos,
            bufferSize);
        return bos;
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

    public String getExtension(final File file) {
        final String name = file.getName();
        final int pos = name.lastIndexOf('.');
        if (-1 < pos) {
            final String extension = name.substring(pos + 1);
            return extension;
        }
        return null;
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

    public void copy(final File from, final File to) {
        walk(from, new CopyWalker(to, this));
    }

    /*
     * ディレクトリを上から順に下層へたどる。
     */
    public void walk(final File entrance, final FileWalker walker) {
        if (entrance.isDirectory()) {
            walkDirectory(entrance, walker);
        } else if (entrance.isFile()) {
            walker.file(entrance);
        }
    }

    private void walkDirectory(final File dir, final FileWalker walker) {
        if (!walker.shouldEnter(dir)) {
            return;
        }

        walker.enter(dir);
        try {
            final File[] children = dir.listFiles();
            if (children == null) {
                return;
            }

            final List<File> files = new ArrayList<File>();
            for (final File child : children) {
                if (child.isDirectory()) {
                    walkDirectory(child, walker);
                } else if (child.isFile()) {
                    files.add(child);
                }
            }

            for (final File file : files) {
                walker.file(file);
            }
        } finally {
            walker.leave(dir);
        }
    }

    public void copyFile(final File from, final File to) {
        BufferedInputStream is = null;
        BufferedOutputStream os = null;
        try {
            is = openBufferedInputStream(from);
            os = openBufferedOutputStream(to);
            pipe(is, os);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(os);
            closeNoException(is);
        }
    }

    public boolean exists(final File parent, final String childPath) {
        final File f = new File(parent, childPath);
        return f.exists();
    }

    public static class DeleteWalker implements FileWalker {

        private static final Logger logger = LoggerFactory.getLogger();

        @Override
        public boolean shouldEnter(final File dir) {
            return true;
        }

        @Override
        public void enter(final File dir) {
        }

        @Override
        public void leave(final File dir) {
            final boolean delete = dir.delete();
            logger.debug("delete directory {} [{}]", delete, dir);
        }

        @Override
        public void file(final File file) {
            final boolean delete = file.delete();
            logger.debug("delete file {} [{}]", delete, file);
        }

    }

    public static class CopyWalker implements FileWalker {

        private static final Logger logger = LoggerFactory.getLogger();
        private final File toDir;
        private File currentToDir;
        private final FileOperation files;

        public CopyWalker(final File toDir, final FileOperation files) {
            this.toDir = toDir;
            toDir.mkdirs();
            this.files = files;
        }

        @Override
        public boolean shouldEnter(final File dir) {
            return true;
        }

        @Override
        public void enter(final File dir) {
            if (currentToDir == null) {
                currentToDir = toDir;
            } else {
                currentToDir = new File(currentToDir, dir.getName());
            }
            currentToDir.mkdir();
            logger.debug("enter into [{}]", currentToDir);
        }

        @Override
        public void leave(final File dir) {
            logger.debug("leave from [{}]", currentToDir);
            currentToDir = currentToDir.getParentFile();
        }

        @Override
        public void file(final File file) {
            final File to = new File(currentToDir, file.getName());
            files.copyFile(file, to);
            logger.debug("copy file [{}] to [{}]", file, to);
        }

    }

}
