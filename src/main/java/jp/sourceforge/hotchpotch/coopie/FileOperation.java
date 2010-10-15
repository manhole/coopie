package jp.sourceforge.hotchpotch.coopie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
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
        deleteFile(f);
        f.mkdir();
        return f;
    }

    public File createTempDir(final File parent) {
        final File f = createTempFile(parent);
        deleteFile(f);
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

    public void write(final File file, final String text) {
        final Writer writer = openBufferedWriter(file);
        try {
            writer.write(text);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(writer);
        }
    }

    public String read(final File file) {
        final BufferedReader reader = openBufferedReader(file);
        final StringWriter writer = new StringWriter();
        try {
            pipe(reader, writer);
            return writer.toString();
        } catch (final IOException e) {
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

    private byte[] readAsBytes(final InputStream is) throws IOException {
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

    private void pipe(final Reader in, final Writer out) throws IOException {
        final char[] buf = new char[bufferSize];
        for (int len = 0; (len = in.read(buf, 0, buf.length)) != -1;) {
            out.write(buf, 0, len);
        }
    }

    public BufferedWriter openBufferedWriter(final File file) {
        final OutputStreamWriter osw = openOutputStreamWriter(file);
        final BufferedWriter writer = new BufferedWriter(osw, bufferSize);
        return writer;
    }

    public BufferedReader openBufferedReader(final File file) {
        final InputStreamReader osw = openInputStreamReader(file);
        final BufferedReader reader = new BufferedReader(osw, bufferSize);
        return reader;
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

    private InputStreamReader openInputStreamReader(final File file) {
        final FileInputStream fis = openInputStream(file);
        try {
            final InputStreamReader isr = new InputStreamReader(fis, encoding);
            return isr;
        } catch (final UnsupportedEncodingException e) {
            closeNoException(fis);
            throw new IORuntimeException(e);
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

    private FileOutputStream openOutputStream(final File file) {
        try {
            final FileOutputStream fos = new FileOutputStream(file);
            return fos;
        } catch (final FileNotFoundException e) {
            throw new FileNotFoundRuntimeException(e, file);
        }
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

    public void delete(final File file) {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            deleteFile(file);
        }
    }

    private void deleteDirectory(final File file) {
        final DeleteWalker deleteWalker = new DeleteWalker(this);
        walk(file, deleteWalker);
        deleteWalker.logResult();
    }

    private boolean deleteFile(final File file) {
        return file.delete();
    }

    public void deleteChildren(final File dir) {
        final DeleteWalker deleteWalker = new DeleteWalker(this);

        walkDescendant(dir, deleteWalker);

        deleteWalker.logResult();
    }

    public void copy(final File from, final File to) {
        if (from.isDirectory()) {
            copyDirectory(from, to);
        } else {
            copyFile(from, to);
        }
    }

    private void copyDirectory(final File from, final File to) {
        final CopyWalker copyWalker = new CopyWalker(to, this);
        walk(from, copyWalker);
        copyWalker.logResult();
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

    public void move(final File from, final File to) {
        if (from.isDirectory()) {
            moveDirectory(from, to);
        } else {
            moveFile(from, to);
        }
    }

    private void moveDirectory(final File from, final File to) {
        final MoveWalker copyWalker = new MoveWalker(to, this);
        walk(from, copyWalker);
        copyWalker.logResult();
    }

    public void moveFile(final File from, final File to) {
        if (from.renameTo(to)) {
        } else {
            copyFile(from, to);
            deleteFile(from);
        }
    }

    /*
     * ディレクトリを上から順に下層へたどる。
     */
    public void walk(final File entrance, final FileWalker walker) {
        if (entrance.isDirectory()) {
            walkDirectory(entrance, walker);
        } else if (entrance.isFile()) {
            walkFile(entrance, walker);
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
                walkFile(file, walker);
            }
        } finally {
            walker.leave(dir);
        }
    }

    private void walkFile(final File file, final FileWalker walker) {
        walker.file(file);
    }

    public void listDescendant(final File parent, final FileCallback callback) {
        final FileWalker fileWalker = new FileCallbackAdapter(callback);
        walkDescendant(parent, fileWalker);
    }

    private void walkDescendant(final File parent, final FileWalker fileWalker) {
        final File[] children = parent.listFiles();
        if (children == null) {
            return;
        }

        for (final File child : children) {
            walk(child, fileWalker);
        }
    }

    public boolean exists(final File parent, final String childPath) {
        final File f = new File(parent, childPath);
        return f.exists();
    }

    public boolean binaryEquals(final File f1, final File f2) {
        BufferedInputStream is1 = null;
        BufferedInputStream is2 = null;
        try {
            is1 = openBufferedInputStream(f1);
            is2 = openBufferedInputStream(f2);
            final boolean ret = binaryEquals(is1, is2);
            return ret;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(is1);
            closeNoException(is2);
        }
    }

    protected boolean binaryEquals(final byte[] b1, final byte[] b2) {
        if (b1.length != b2.length) {
            return false;
        }
        for (int i = 0; i < b1.length; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    protected boolean binaryEquals(final InputStream is1, final InputStream is2)
        throws IOException {
        final byte[] bytes1 = new byte[bufferSize];
        final byte[] bytes2 = new byte[bufferSize];
        for (;;) {
            final int read1 = is1.read(bytes1);
            final int read2 = is2.read(bytes2);
            // サイズが違う
            if (read1 != read2) {
                return false;
            }
            // データが違う
            if (!binaryEquals(bytes1, bytes2)) {
                return false;
            }
            // 最後まで読んだ
            if ((read1 < 0) || (read2 < 0)) {
                break;
            }
        }
        return true;
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

    public boolean containsPath(final File file, final String path) {
        final String cFilePath = getCanonicalPath(file);
        final String cPath = toCanonicalPath(path);
        if (cFilePath.contains(cPath)) {
            return true;
        }
        return false;
    }

    public boolean matchPath(final File file, final String pattern) {
        final String cFilePath = getCanonicalPath(file);
        final boolean matches = cFilePath.matches(pattern);
        return matches;
    }

    private String getCanonicalPath(final File file) {
        try {
            final String path = file.getCanonicalPath();
            return toCanonicalPath(path);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private String toCanonicalPath(final String path) {
        if (path != null) {
            return path.replace('\\', '/');
        }
        return null;
    }

    private static class FileCallbackAdapter implements FileWalker {

        private final FileCallback callback;

        FileCallbackAdapter(final FileCallback callback) {
            this.callback = callback;
        }

        @Override
        public void enter(final File dir) {
            try {
                callback.callback(dir);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void file(final File file) {
            try {
                callback.callback(file);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void leave(final File dir) {
        }

        @Override
        public boolean shouldEnter(final File dir) {
            return true;
        }

    }

    public static class DeleteWalker implements FileWalker {

        private static final Logger logger = LoggerFactory.getLogger();

        private final FileOperation files;
        private int deletedFileCount;
        private int deletedDirCount;
        private long deletedTotalBytes;

        public DeleteWalker(final FileOperation files) {
            this.files = files;
        }

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
            if (delete) {
                deletedDirCount++;
            }
            logger.debug("delete directory {} [{}]", delete, dir);
        }

        @Override
        public void file(final File file) {
            final long len = file.length();
            final boolean delete = files.deleteFile(file);
            if (delete) {
                deletedFileCount++;
                deletedTotalBytes += len;
            }
            logger.debug("delete file {} [{}]", delete, file);
        }

        public void logResult() {
            logger.debug("DeleteResult: files={}, dirs={}, bytes={}",
                new Object[] { deletedFileCount, deletedDirCount,
                    deletedTotalBytes });
        }

    }

    public static class CopyWalker implements FileWalker {

        private static final Logger logger = LoggerFactory.getLogger();
        protected final File toDir;
        protected File currentToDir;
        protected final FileOperation files;
        protected int copiedFileCount;
        protected int createdDirCount;
        protected long copiedTotalBytes;

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
                // いちばん最初
                currentToDir = toDir;
            } else {
                // 2度目以降
                currentToDir = new File(currentToDir, dir.getName());
            }
            if (!currentToDir.exists()) {
                currentToDir.mkdir();
                createdDirCount++;
            }
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
            final long len = file.length();
            files.copyFile(file, to);
            copiedFileCount++;
            copiedTotalBytes += len;
            logger.debug("copy file [{}] to [{}]", file, to);
        }

        public void logResult() {
            logger.debug("CopyResult: files={}, dirs={}, bytes={}",
                new Object[] { copiedFileCount, createdDirCount,
                    copiedTotalBytes });
        }

    }

    public static class MoveWalker implements FileWalker {

        private final CopyWalker copyWalker;
        private final DeleteWalker deleteWalker;

        public MoveWalker(final File toDir, final FileOperation files) {
            copyWalker = new CopyWalker(toDir, files);
            deleteWalker = new DeleteWalker(files);
        }

        @Override
        public boolean shouldEnter(final File dir) {
            return true;
        }

        @Override
        public void enter(final File dir) {
            copyWalker.enter(dir);
            deleteWalker.enter(dir);
        }

        @Override
        public void leave(final File dir) {
            copyWalker.leave(dir);
            deleteWalker.leave(dir);
        }

        @Override
        public void file(final File file) {
            copyWalker.file(file);
            deleteWalker.file(file);
        }

        public void logResult() {
            copyWalker.logResult();
            deleteWalker.logResult();
        }

    }

}
