package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junitx.framework.ArrayAssert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.t2framework.commons.exception.IORuntimeException;

public class FileOperationTest {

    private static final Logger logger = LoggerFactory.getLogger();

    private File root;

    @Before
    public void setUp() throws Throwable {
        root = new FileOperation().createTempDir();
    }

    @After
    public void tearDown() {
        new FileOperation().delete(root);
    }

    @Test
    public void createTempDir() throws Exception {
        // ## Arrange ##
        // ## Act ##
        final File f = new FileOperation().createTempDir();
        logger.debug("tempDir={}", f);

        // ## Assert ##
        assertEquals(true, f.exists());
        assertEquals(true, f.isDirectory());
        assertEquals(false, f.isFile());
        f.delete();
    }

    @Test
    public void write() throws Exception {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f = files.createTempFile(root);

        // ## Act ##
        files.write(f, "かきくえば");
        files.write(f, "かねがなるなり");

        // ## Assert ##
        final byte[] bytes = files.readAsBytes(f);
        final String s = new String(bytes, "UTF-8");
        assertEquals("かねがなるなり", s);
    }

    @Test
    public void read() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f = files.createTempFile(root);

        files.write(f, "かきくえば");

        // ## Act ##
        final String read = files.read(f);

        // ## Assert ##
        assertEquals("かきくえば", read);
    }

    @Test
    public void walk1() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f1 = files.createTempFile(root);
        final File d1 = files.createDirectory(root, "d1");
        final File d2 = files.createDirectory(root, "d2");
        final File d3 = files.createDirectory(d2, "d3");
        final File f2 = files.createTempFile(d1);

        final MockFileWalker walker = new MockFileWalker();

        // ## Act ##
        files.walk(root, walker);

        // ## Assert ##
        final List<MethodCallEvent> events = walker.getEvents();
        assertEquals(14, events.size());

        final Iterator<MethodCallEvent> it = events.iterator();
        it.next().assertEquals("shouldEnter", root);
        it.next().assertEquals("enter", root);
        it.next().assertEquals("shouldEnter", d1);
        it.next().assertEquals("enter", d1);
        it.next().assertEquals("file", f2);
        it.next().assertEquals("leave", d1);
        it.next().assertEquals("shouldEnter", d2);
        it.next().assertEquals("enter", d2);
        it.next().assertEquals("shouldEnter", d3);
        it.next().assertEquals("enter", d3);
        it.next().assertEquals("leave", d3);
        it.next().assertEquals("leave", d2);
        it.next().assertEquals("file", f1);
        it.next().assertEquals("leave", root);
        assertEquals(false, it.hasNext());
    }

    /*
     * Fileを引数に渡した場合
     */
    @Test
    public void walk2() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f1 = files.createTempFile(root);

        final MockFileWalker walker = new MockFileWalker();

        // ## Act ##
        files.walk(f1, walker);

        // ## Assert ##
        final List<MethodCallEvent> events = walker.getEvents();

        final Iterator<MethodCallEvent> it = events.iterator();
        it.next().assertEquals("file", f1);
        assertEquals(false, it.hasNext());
    }

    /*
     * shouldEnterがfalseを返す場合
     */
    @Test
    public void walk3() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f1 = files.createTempFile(root);
        final File d1 = files.createDirectory(root, "d1");
        final File d2 = files.createDirectory(root, "d2");
        final File d3 = files.createDirectory(d2, "d3");
        final File f2 = files.createTempFile(d1);

        final MockFileWalker walker = new MockFileWalker() {
            @Override
            public boolean shouldEnter(final File dir) {
                final boolean shouldEnter = super.shouldEnter(dir);
                /*
                 * d2より下には入らない
                 */
                if (d2.equals(dir)) {
                    return false;
                }
                return shouldEnter;
            }
        };

        // ## Act ##
        files.walk(root, walker);

        // ## Assert ##
        final List<MethodCallEvent> events = walker.getEvents();
        assertEquals(9, events.size());

        final Iterator<MethodCallEvent> it = events.iterator();
        it.next().assertEquals("shouldEnter", root);
        it.next().assertEquals("enter", root);
        it.next().assertEquals("shouldEnter", d1);
        it.next().assertEquals("enter", d1);
        it.next().assertEquals("file", f2);
        it.next().assertEquals("leave", d1);
        it.next().assertEquals("shouldEnter", d2);
        //        it.next().assertEquals("enter", d2);
        //        it.next().assertEquals("shouldEnter", d3);
        //        it.next().assertEquals("enter", d3);
        //        it.next().assertEquals("leave", d3);
        //        it.next().assertEquals("leave", d2);
        it.next().assertEquals("file", f1);
        it.next().assertEquals("leave", root);
        assertEquals(false, it.hasNext());
    }

    private static class MockFileWalker implements FileWalker {

        final List<MethodCallEvent> events = new ArrayList<MethodCallEvent>();

        public List<MethodCallEvent> getEvents() {
            return events;
        }

        @Override
        public boolean shouldEnter(final File dir) {
            events.add(new MethodCallEvent("shouldEnter", dir));
            return true;
        }

        @Override
        public void enter(final File dir) {
            events.add(new MethodCallEvent("enter", dir));
        }

        @Override
        public void leave(final File dir) {
            events.add(new MethodCallEvent("leave", dir));
        }

        @Override
        public void file(final File file) {
            events.add(new MethodCallEvent("file", file));
        }

    }

    private static class MethodCallEvent {

        final String name;
        final Object[] args;

        MethodCallEvent(final String name, final Object... args) {
            this.name = name;
            this.args = args;
        }

        public void assertEquals(final String name, final Object... args) {
            Assert.assertEquals(name, this.name);
            ArrayAssert.assertEquals(args, this.args);
        }

    }

    @Test
    public void getExtension() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        // ## Act ##
        // ## Assert ##
        assertEquals("txt", files.getExtension(new File("foo.txt")));
        assertEquals("Txt", files.getExtension(new File("bar.Txt")));
        assertEquals("txt", files.getExtension(new File("foo.bar.txt")));
        assertEquals("gz", files.getExtension(new File("foo.bar.tar.gz")));
        assertEquals("cvsignore", files.getExtension(new File(".cvsignore")));
        assertEquals("", files.getExtension(new File("foo.bar.tar.")));
        assertEquals(null, files.getExtension(new File("foo")));
    }

    @Test
    public void delete_file() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f1 = files.createTempFile(root);
        files.write(f1, "1234");

        // ## Act ##
        files.delete(f1);

        // ## Assert ##
        assertEquals(false, f1.exists());
        assertEquals(true, root.exists());
    }

    @Test
    public void delete_directory() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File f1 = files.createTempFile(root);
        final File d1 = files.createDirectory(root, "d1");
        final File d2 = files.createDirectory(root, "d2");
        final File d3 = files.createDirectory(d2, "d3");
        final File f2 = files.createTempFile(d1);
        final File f3 = files.createTempFile(d1);
        files.write(f2, "1234");
        files.write(f3, "aiueo");

        // ## Act ##
        files.delete(d1);

        // ## Assert ##
        assertEquals(false, d1.exists());
        assertEquals(true, root.exists());
    }

    /*
     * ＜copyFileの挙動について＞
     * 
     * ファイルをコピーする。
     * 
     * copy先がディレクトリの場合(同名のディレクトリがある場合)はエラーになる。
     * (そのディレクトリ内へcopyはしない。)
     * ディレクトリ内へcopyしたかったら、copy先として"toDir/toFilename"を指定すべき。
     * 
     * ディレクトリのコピーではない。
     * fromにディレクトリが指定された場合はエラーになる。
     * 
     * 
     * ＜moveFileの挙動について＞
     * 
     * 基本的にcopyFileと同じで、違う点はfrom側が消えること。
     * 
     * move先がディレクトリの場合はエラーになる。
     * (そのディレクトリ内へmoveはしない。)
     * ディレクトリ内へmoveしたかったら、move先として"toDir/toFilename"を指定すべき。
     * 
     * TODO ディレクトリのcopyでは無い、とするか?
     */
    /*
     * ＜copyの挙動について＞
     * 
     * 基本的にcopyDirectoryのこと。
     * 
     * toがあろうと無かろうと同じ挙動をする。
     * 
     * "from"→"to"へcopyするとき、
     * "from/foo"は"to/foo"へcopyされる。
     * 
     * "from"をディレクトリとしてcopy先へ作りたいなら、
     * "to/from"をcopy先として指定すべき。
     * 
     * copy先に既に同名のディレクトリが存在する場合は、エラーとする。
     */
    /*
     * ＜moveの挙動について＞
     * 
     * 基本的にcopyと同じで、違う点はfrom側が消えること。
     * toがあろうと無かろうと同じ挙動をする。
     * 
     * renameToと同じ挙動を目指す。
     */

    /*
     * fromがあり、toが無い。toの親ディレクトリは存在する。
     */
    @Test
    public void copyFile1() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = new File(root, "f2.txt");

        files.write(from, "ほげ");

        // ## Act ##
        files.copyFile(from, to);

        // ## Assert ##
        assertEquals(true, from.exists());
        assertEquals("ほげ", files.read(to));
    }

    @Test
    public void copy_file1() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = new File(root, "f2.txt");

        files.write(from, "ほげ");

        // ## Act ##
        files.copy(from, to);

        // ## Assert ##
        assertEquals(true, from.exists());
        assertEquals("ほげ", files.read(to));
    }

    @Test
    public void moveFile1() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = new File(root, "f2.txt");

        files.write(from, "ほげ");

        // ## Act ##
        files.moveFile(from, to);

        // ## Assert ##
        assertEquals(false, from.exists());
        assertEquals("ほげ", files.read(to));
    }

    /*
     * copy先に同名のファイルがあったら、上書きする。
     */
    @Test
    public void copyFile2() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = files.createFile(root, "f2.txt");

        files.write(from, "あいうえお");
        files.write(to, "あかさたな");
        assertEquals("あかさたな", files.read(to));

        // ## Act ##
        files.copyFile(from, to);

        // ## Assert ##
        assertEquals(true, from.exists());
        assertEquals("あいうえお", files.read(to));
    }

    @Test
    public void moveFile2() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = files.createFile(root, "f2.txt");

        files.write(from, "あいうえお");
        files.write(to, "あかさたな");
        assertEquals("あかさたな", files.read(to));

        // ## Act ##
        files.moveFile(from, to);

        // ## Assert ##
        assertEquals(false, from.exists());
        assertEquals("あいうえお", files.read(to));
    }

    /*
     * copy先に既に同名のディレクトリが存在する場合は、エラーとする。
     */
    @Test
    public void copyFile3() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = files.createDirectory(root, "f2.txt");

        files.write(from, "aaa");

        // ## Act ##
        // ## Assert ##
        try {
            files.copyFile(from, to);
            fail();
        } catch (final IORuntimeException e) {
            logger.debug(e.getMessage());
        }
        assertEquals(true, from.exists());
    }

    @Test
    public void moveFile3() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = files.createDirectory(root, "f2.txt");

        files.write(from, "aaa");

        // ## Act ##
        // ## Assert ##
        try {
            files.moveFile(from, to);
            fail();
        } catch (final IORuntimeException e) {
            logger.debug(e.getMessage());
        }
        assertEquals(true, from.exists());
    }

    /*
     * "to"だけがある場合
     * 
     * "to"側に"from"という名前のディレクトリは作られない。
     * "from"の中身が"to"の中にできる。
     */
    @Test
    public void copy_directory1() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = files.createDirectory(root, "to");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげ");

        assertEquals(true, files.exists(from, "l1"));
        assertEquals(true, files.exists(from, "l1/l2"));
        assertEquals(true, files.exists(from, "l1/l2/f1.txt"));

        // ## Act ##
        files.copy(from, to);

        // ## Assert ##
        assertEquals(true, from.exists());
        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

    /*
     * "to"だけがある場合
     * 
     * "to"側に"from"という名前のディレクトリは作られない。
     * "from"の中身が"to"の中にできる。
     */
    @Test
    public void move_directory1() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = files.createDirectory(root, "to");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげ");

        assertEquals(true, files.exists(from, "l1"));
        assertEquals(true, files.exists(from, "l1/l2"));
        assertEquals(true, files.exists(from, "l1/l2/f1.txt"));

        // ## Act ##
        files.move(from, to);

        // ## Assert ##
        assertEquals(false, from.exists());
        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

    /*
     * "to"ディレクトリがまだ存在しない場合
     */
    @Test
    public void copy_directory2() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = new File(root, "to");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげ2");

        assertEquals(false, to.exists());

        // ## Act ##
        files.copy(from, to);

        // ## Assert ##
        assertEquals(true, from.exists());
        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

    /*
     * "to"ディレクトリがまだ存在しない場合
     */
    @Test
    public void move_directory2() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = new File(root, "to");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげ2");

        assertEquals(false, to.exists());

        // ## Act ##
        files.move(from, to);

        // ## Assert ##
        assertEquals(false, from.exists());
        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

    /*
     * "path/to/child"ディレクトリがまだ存在しない場合
     */
    @Test
    public void copy_directory3() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = new File(root, "path/to/child");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげげ");

        final File f2 = files.createFile(c1, "f2.txt");
        files.write(f2, "ほげほげ");

        // ## Act ##
        files.copy(from, to);

        // ## Assert ##
        assertEquals(true, from.exists());
        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
        assertEquals(true, files.exists(to, "l1/f2.txt"));

        assertEquals("ほげげ", files.read(new File(to, "l1/l2/f1.txt")));
        assertEquals("ほげほげ", files.read(new File(to, "l1/f2.txt")));
    }

    /*
     * "path/to/child"ディレクトリがまだ存在しない場合
     */
    @Test
    public void move_directory3() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = new File(root, "path/to/child");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげげ");

        final File f2 = files.createFile(c1, "f2.txt");
        files.write(f2, "ほげほげ");

        // ## Act ##
        files.move(from, to);

        // ## Assert ##
        assertEquals(false, from.exists());
        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
        assertEquals(true, files.exists(to, "l1/f2.txt"));

        assertEquals("ほげげ", files.read(new File(to, "l1/l2/f1.txt")));
        assertEquals("ほげほげ", files.read(new File(to, "l1/f2.txt")));
    }

    /*
     * copy先に既に同名のディレクトリが存在する場合は、エラーとする。
     */
    @Test
    public void copy_directory4() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File from = files.createFile(root, "f1.txt");
        final File to = files.createDirectory(root, "to");

        files.write(from, "aaa");

        // ## Act ##
        // ## Assert ##
        try {
            files.copy(from, to);
            fail();
        } catch (final IORuntimeException e) {
            logger.debug(e.getMessage());
        }
    }

    @Test
    public void binaryEquals() throws Exception {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        final File f1 = new File(root, "f1.txt");
        final File f2 = new File(root, "f2.txt");
        final File f3 = new File(root, "f3.txt");
        final File f4 = new File(root, "f4.txt");

        files.write(f1, "abcdf");
        files.write(f2, "Abcdf");
        files.write(f3, "abcdf");
        files.write(f4, "abcdf ");

        // ## Act ##
        // ## Assert ##
        assertEquals(false, files.binaryEquals(f1, f2));
        assertEquals(true, files.binaryEquals(f1, f3));
        assertEquals(false, files.binaryEquals(f1, f4));
    }

    @Test
    public void binaryEquals_byte() throws Exception {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        // ## Act ##
        // ## Assert ##
        assertEquals(true, files.binaryEquals("12345".getBytes(), "12345"
            .getBytes()));
        assertEquals(false, files.binaryEquals("12345".getBytes(), "123456"
            .getBytes()));
        assertEquals(false, files.binaryEquals("1234".getBytes(), "1235"
            .getBytes()));
        assertEquals(false, files.binaryEquals("1234".getBytes(), "123 "
            .getBytes()));
        assertEquals(false, files.binaryEquals("1234".getBytes(), "123"
            .getBytes()));
    }

    @Test
    public void binaryEquals_inputStream() throws Exception {
        // ## Arrange ##
        final FileOperation files = new FileOperation();

        // ## Act ##
        // ## Assert ##
        {
            final ByteArrayInputStream is1 = new ByteArrayInputStream("12345"
                .getBytes());
            final ByteArrayInputStream is2 = new ByteArrayInputStream("12345"
                .getBytes());
            assertEquals(true, files.binaryEquals(is1, is2));
        }
        {
            final ByteArrayInputStream is1 = new ByteArrayInputStream("12345"
                .getBytes());
            final ByteArrayInputStream is2 = new ByteArrayInputStream("123456"
                .getBytes());
            assertEquals(false, files.binaryEquals(is1, is2));
        }
        {
            final ByteArrayInputStream is1 = new ByteArrayInputStream("1234567"
                .getBytes());
            final ByteArrayInputStream is2 = new ByteArrayInputStream("123456"
                .getBytes());
            assertEquals(false, files.binaryEquals(is1, is2));
        }
        {
            final ByteArrayInputStream is1 = new ByteArrayInputStream("1234"
                .getBytes());
            final ByteArrayInputStream is2 = new ByteArrayInputStream("1235"
                .getBytes());
            assertEquals(false, files.binaryEquals(is1, is2));
        }
    }

}
