package jp.sourceforge.hotchpotch.coopie;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.t2framework.commons.util.ResourceUtil;

public class Sha1Test {

    @Test
    public void test1() throws Throwable {
        // ## Arrange ##
        final File file = ResourceUtil.getResourceAsFile(Sha1Test.class
                .getPackage().getName().replace('.', '/')
                + "/coopie-0.1.0-20100708.043157-1.pom");

        // ## Act ##
        final String actual = new Sha1().digest(file);

        // ## Assert ##
        assertEquals("e31ef5b931e16ce308cbad3b5c96360e1a1ea619", actual);
    }

}
