package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        System.out.println(f);

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
    public void delete() throws Throwable {
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
     * ＜COPY FILEの挙動について＞
     * 
     * copy先がディレクトリの場合はエラーになる。
     * (そのディレクトリ内へcopyはしない。)
     * ディレクトリ内へcopyしたかったら、copy先として"toDir/toFilename"を指定すべき。
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
        assertEquals("ほげ", files.read(to));
    }

    /*
     * copy先に、既に同名のディレクトリが存在する場合は、エラーとする。
     */
    @Test
    public void copyFile2() throws Throwable {
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
    }

    /*
     * ＜COPYの挙動について＞
     * 
     * toがあろうと無かろうと同じ挙動をする。
     * 
     * "from"→"to"へcopyするとき、
     * "from/foo"は"to/foo"へcopyされる。
     * 
     * "from"をディレクトリとしてcopy先へ作りたいなら、
     * "to/from"をcopy先として指定すべき。
     */

    /*
     * "to"だけがある場合
     * 
     * "to"側に"from"という名前のディレクトリは作られない。
     * "from"の中身が"to"の中にできる。
     */
    @Test
    public void copyDirectory1() throws Throwable {
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

        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

    /*
     * "to"ディレクトリがまだ存在しない場合
     */
    @Test
    public void copyDirectory2() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = new File(root, "to");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげ2");

        // ## Act ##
        files.copy(from, to);

        // ## Assert ##

        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

    /*
     * "path/to/child"ディレクトリがまだ存在しない場合
     */
    @Test
    public void copyDirectory3() throws Throwable {
        // ## Arrange ##
        final FileOperation files = new FileOperation();
        final File from = files.createDirectory(root, "from");
        final File to = new File(root, "path/to/child");

        final File c1 = files.createDirectory(from, "l1");
        final File c2 = files.createDirectory(c1, "l2");

        final File f1 = files.createFile(c2, "f1.txt");
        files.write(f1, "ほげ");

        // ## Act ##
        files.copy(from, to);

        // ## Assert ##

        assertEquals(true, to.exists());
        assertEquals(true, files.exists(to, "l1"));
        assertEquals(true, files.exists(to, "l1/l2"));
        assertEquals(true, files.exists(to, "l1/l2/f1.txt"));
    }

}
