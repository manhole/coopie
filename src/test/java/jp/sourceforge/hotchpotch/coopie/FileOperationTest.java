package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.io.File;

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

}
