package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junitx.framework.ArrayAssert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileOperationTest {

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

        // ## Act ##
        files.delete(d1);

        // ## Assert ##
        assertEquals(false, d1.exists());
        assertEquals(true, root.exists());
    }

}
