package jp.sourceforge.hotchpotch.coopie.csv;

import static jp.sourceforge.hotchpotch.coopie.VarArgs.a;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public abstract class CsvElementReaderTest {

    protected abstract CsvElementReader constructTest1Reader() throws Throwable;

    @Test
    public void assertRead1() throws Throwable {
        // ## Arrange ##
        final CsvElementReader reader = constructTest1Reader();

        // ## Act ##
        // ## Assert ##
        /*
         * 初期値は"-1"
         */
        assertEquals(-1, reader.getRecordNo());
        assertArrayEquals(a("aaa", "ccc", "bbb"), reader.readRecord());
        assertEquals(0, reader.getRecordNo());
        assertArrayEquals(a("あ1", "う1", "い1"), reader.readRecord());
        assertEquals(1, reader.getRecordNo());
        assertArrayEquals(a("あ2", "う2", "い2"), reader.readRecord());
        assertEquals(2, reader.getRecordNo());
        assertArrayEquals(a("あ3", "う3", "い3"), reader.readRecord());
        assertEquals(3, reader.getRecordNo());
        assertNull(reader.readRecord());
        /*
         * 最後まで呼んだ後はカウントアップしない
         */
        assertEquals(3, reader.getRecordNo());
        assertEquals(3, reader.getRecordNo());

        reader.close();
    }

}
