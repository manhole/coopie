/*
 * Copyright 2010 manhole
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package jp.sourceforge.hotchpotch.coopie.util;

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
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import jp.sourceforge.hotchpotch.coopie.logging.LoggerFactory;

import org.slf4j.Logger;
import org.t2framework.commons.exception.FileNotFoundRuntimeException;
import org.t2framework.commons.exception.IORuntimeException;

public class FileOperation {

    private static final int K = 1024;
    private static final int M = K * 1024;
    private static final int DEFAULT_BUFF_SIZE = K * 8;
    private static final String UTF8 = "UTF-8";
    // FileOPeration
    private String prefix_ = "fop";
    private String suffix_ = ".tmp";
    private int bufferSize_ = DEFAULT_BUFF_SIZE;
    private Charset charset_;
    private final BinaryStreamOperation binaryStreams_ = new BinaryStreamOperation();

    public FileOperation() {
        setEncoding(UTF8);
    }

    public File createTempFile() {
        final File f = createTempFile(prefix_);
        return f;
    }

    public File createTempFile(final String prefix) {
        try {
            final File f = File.createTempFile(prefix, suffix_);
            return f;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public File createTempFile(final File parent) {
        try {
            final File f = File.createTempFile(prefix_, suffix_, parent);
            return f;
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    public File createTempDir() {
        final File f = createTempFile();
        deleteFileInternal(f);
        f.mkdir();
        return f;
    }

    public File createTempDir(final String prefix) {
        final File f = createTempFile(prefix);
        deleteFileInternal(f);
        f.mkdir();
        return f;
    }

    public File createTempDir(final File parent) {
        final File f = createTempFile(parent);
        deleteFileInternal(f);
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

    public void write(final File file, final InputStream is) {
        final OutputStream os = openBufferedOutputStream(file);
        try {
            pipe(is, os);
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        } finally {
            closeNoException(is);
            closeNoException(os);
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
        } finally {
            closeNoException(reader);
            closeNoException(writer);
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
        binaryStreams_.pipe(is, os);
    }

    private void pipe(final Reader in, final Writer out) throws IOException {
        final char[] buf = new char[bufferSize_];
        for (int len = 0; (len = in.read(buf, 0, buf.length)) != -1;) {
            out.write(buf, 0, len);
        }
    }

    public BufferedWriter openBufferedWriter(final File file) {
        final OutputStreamWriter osw = openOutputStreamWriter(file);
        final BufferedWriter writer = new BufferedWriter(osw, bufferSize_);
        return writer;
    }

    public BufferedReader openBufferedReader(final File file) {
        final InputStreamReader osw = openInputStreamReader(file);
        final BufferedReader reader = new BufferedReader(osw, bufferSize_);
        return reader;
    }

    private OutputStreamWriter openOutputStreamWriter(final File file) {
        final FileOutputStream fos = openOutputStream(file);
        final OutputStreamWriter osw = new OutputStreamWriter(fos, charset_);
        return osw;
    }

    private InputStreamReader openInputStreamReader(final File file) {
        final FileInputStream fis = openInputStream(file);
        final InputStreamReader isr = new InputStreamReader(fis, charset_);
        return isr;
    }

    public BufferedOutputStream openBufferedOutputStream(final File file) {
        final FileOutputStream fos = openOutputStream(file);
        final BufferedOutputStream bos = new BufferedOutputStream(fos,
                bufferSize_);
        return bos;
    }

    public BufferedInputStream openBufferedInputStream(final File file) {
        final FileInputStream fis = openInputStream(file);
        final BufferedInputStream bis = new BufferedInputStream(fis,
                bufferSize_);
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
        CloseableUtil.closeNoException(closeable);
    }

    public String getExtension(final File file) {
        final FileResource r = getFileResource(file);
        return r.getExtension();
    }

    public FileResource getFileResource(final File file) {
        return new FileResourceImpl(file);
    }

    public DeleteResult delete(final File file) {
        if (file.isDirectory()) {
            return deleteDirectory(file);
        } else {
            return deleteFile(file);
        }
    }

    private DeleteResult deleteDirectory(final File dir) {
        final DeleteWalker deleteWalker = new DeleteWalker(this);
        walk(dir, deleteWalker);
        final DeleteResult result = deleteWalker.getResult();
        result.logResult();
        return result;
    }

    private DeleteResult deleteFile(final File file) {
        final DeleteWalker deleteWalker = new DeleteWalker(this);
        walk(file, deleteWalker);
        final DeleteResult result = deleteWalker.getResult();
        result.logResult();
        return result;
    }

    private boolean deleteFileInternal(final File file) {
        return file.delete();
    }

    public DeleteResult deleteChildren(final File dir) {
        final DeleteWalker deleteWalker = new DeleteWalker(this);
        walkDescendant(dir, deleteWalker);
        final DeleteResult result = deleteWalker.getResult();
        result.logResult();
        return result;
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
            deleteFileInternal(from);
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
            final File[] children = listFiles(dir);
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

    private File[] listFiles(final File dir) {
        final File[] files = dir.listFiles();
        Arrays.sort(files, FileNameComparator.getInstance());
        return files;
    }

    private void walkFile(final File file, final FileWalker walker) {
        walker.file(file);
    }

    public void listDescendant(final File parent, final Callback callback) {
        final FileWalker fileWalker = new FileCallbackAdapter(callback);
        walkDescendant(parent, fileWalker);
    }

    private void walkDescendant(final File parent, final FileWalker fileWalker) {
        final File[] children = listFiles(parent);
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
        final byte[] bytes1 = new byte[bufferSize_];
        final byte[] bytes2 = new byte[bufferSize_];
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
            if (read1 < 0 || read2 < 0) {
                break;
            }
        }
        return true;
    }

    public int getBufferSize() {
        return bufferSize_;
    }

    public void setBufferSize(final int bufferSize) {
        bufferSize_ = bufferSize;
        binaryStreams_.setBufferSize(bufferSize);
    }

    public void setEncoding(final String encoding) {
        final Charset found = Charset.forName(encoding);
        setCharset(found);
    }

    public void setCharset(final Charset charset) {
        charset_ = charset;
    }

    public void setSuffix(final String suffix) {
        suffix_ = suffix;
    }

    public void setPrefix(final String prefix) {
        prefix_ = prefix;
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

        private final Callback<File, IOException> callback_;

        FileCallbackAdapter(final Callback<File, IOException> callback) {
            callback_ = callback;
        }

        @Override
        public void enter(final File dir) {
            try {
                callback_.callback(dir);
            } catch (final IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void file(final File file) {
            try {
                callback_.callback(file);
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

        private final DeleteResultCollector collector_;
        private final FileOperation files_;

        public DeleteWalker(final FileOperation files) {
            this(files, new DefaultDeleteResult());
        }

        public DeleteWalker(final FileOperation files,
                final DeleteResultCollector collector) {
            files_ = files;
            collector_ = collector;
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
            final boolean delete = files_.deleteFileInternal(dir);
            if (delete) {
                collector_.deleteDir();
            } else {
                collector_.failureDir();
            }
            logger.debug("delete directory {} [{}]", delete, dir);
        }

        @Override
        public void file(final File file) {
            final long len = file.length();
            final boolean delete = files_.deleteFileInternal(file);
            if (delete) {
                collector_.deleteFile(len);
            } else {
                collector_.failureFile();
            }
            logger.debug("delete file {} [{}]", delete, file);
        }

        public DeleteResult getResult() {
            return collector_.getResult();
        }

        public static class DefaultDeleteResult implements DeleteResult,
                DeleteResultCollector {

            private static final Logger logger = LoggerFactory.getLogger();

            private int fileCount_;
            private int dirCount_;
            private int fileFailureCount_;
            private int dirFailureCount_;
            private long totalBytes_;

            @Override
            public void deleteDir() {
                dirCount_++;
            }

            @Override
            public void deleteFile(final long size) {
                fileCount_++;
                totalBytes_ += size;
            }

            @Override
            public void failureFile() {
                fileFailureCount_++;
            }

            @Override
            public void failureDir() {
                dirFailureCount_++;
            }

            @Override
            public ByteSize getDeletedTotalSize() {
                return ByteSize.create(totalBytes_);
            }

            @Override
            public int getDeletedDirCount() {
                return dirCount_;
            }

            @Override
            public int getDeletedFileCount() {
                return fileCount_;
            }

            @Override
            public void logResult() {
                logger.debug("DeleteResult: files={}, dirs={}, size={}",
                        new Object[] { fileCount_, dirCount_,
                                getDeletedTotalSize() });
            }

            @Override
            public int getFileFailureCount() {
                return fileFailureCount_;
            }

            @Override
            public int getDirFailureCount() {
                return dirFailureCount_;
            }

            @Override
            public boolean hasFailure() {
                return 0 < fileFailureCount_ || 0 < dirFailureCount_;
            }

            @Override
            public DeleteResult getResult() {
                return this;
            }

        }

    }

    public static class CopyWalker implements FileWalker {

        private static final Logger logger = LoggerFactory.getLogger();
        private final File toDir_;
        private File currentToDir_;
        private final FileOperation files_;
        private int copiedFileCount_;
        private int createdDirCount_;
        private long copiedTotalBytes_;

        public CopyWalker(final File toDir, final FileOperation files) {
            toDir_ = toDir;
            toDir_.mkdirs();
            files_ = files;
        }

        @Override
        public boolean shouldEnter(final File dir) {
            return true;
        }

        @Override
        public void enter(final File dir) {
            if (currentToDir_ == null) {
                // いちばん最初
                currentToDir_ = toDir_;
            } else {
                // 2度目以降
                currentToDir_ = new File(currentToDir_, dir.getName());
            }
            if (!currentToDir_.exists()) {
                currentToDir_.mkdir();
                createdDirCount_++;
            }
            logger.debug("enter into [{}]", currentToDir_);
        }

        @Override
        public void leave(final File dir) {
            logger.debug("leave from [{}]", currentToDir_);
            currentToDir_ = currentToDir_.getParentFile();
        }

        @Override
        public void file(final File file) {
            final File to = new File(currentToDir_, file.getName());
            final long len = file.length();
            files_.copyFile(file, to);
            copiedFileCount_++;
            copiedTotalBytes_ += len;
            logger.debug("copy file [{}] to [{}]", file, to);
        }

        public void logResult() {
            logger.debug("CopyResult: files={}, dirs={}, bytes={}",
                    new Object[] { copiedFileCount_, createdDirCount_,
                            copiedTotalBytes_ });
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
            deleteWalker.getResult().logResult();
        }

    }

    static class FileResourceImpl implements FileResource {

        private final File file_;
        private String extensionPrefix_;
        private String extension_;

        public FileResourceImpl(final File file) {
            file_ = file;

            final String name = file.getName();
            final int pos = name.lastIndexOf('.');
            if (0 == pos) {
                /*
                 * 先頭が"."
                 */
                extensionPrefix_ = null;
                extension_ = name.substring(1);
            } else if (1 <= pos) {
                extensionPrefix_ = name.substring(0, pos);
                extension_ = name.substring(pos + 1);
            } else {
                extensionPrefix_ = name;
                extension_ = null;
            }
        }

        @Override
        public String getPrefix() {
            return extensionPrefix_;
        }

        @Override
        public String getExtension() {
            return extension_;
        }

    }

    public static class FileNameComparator implements Comparator<File> {

        private static FileNameComparator INSTANCE = new FileNameComparator();

        public static FileNameComparator getInstance() {
            return INSTANCE;
        }

        @Override
        public int compare(final File o1, final File o2) {
            return o1.getName().compareTo(o2.getName());
        }

    }

    public interface DeleteResultCollector {

        void deleteDir();

        void deleteFile(long size);

        void failureFile();

        void failureDir();

        DeleteResult getResult();

    }

    /*
     * read only interface
     */
    public interface DeleteResult {

        void logResult();

        ByteSize getDeletedTotalSize();

        int getDeletedDirCount();

        int getDeletedFileCount();

        int getDirFailureCount();

        int getFileFailureCount();

        boolean hasFailure();

    }

}
