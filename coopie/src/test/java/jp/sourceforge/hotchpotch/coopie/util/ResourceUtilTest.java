package jp.sourceforge.hotchpotch.coopie.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.nio.file.Path;

import org.junit.Test;

public class ResourceUtilTest {

    @Test
    public void test_getResourceAsPath() throws Throwable {
        final Path path = ResourceUtil.getResourceAsPath(
                "jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest.class");
        // gradleの場合はこういうパスになる
        assertThat(normalize(path),
                is(endsWith("/build/classes/java/test/jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest.class")));

    }

    @Test
    public void test_getResourceAsFile() throws Throwable {
        final File file = ResourceUtil.getResourceAsFile(
                "jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest.class");
        // gradleの場合はこういうパスになる
        assertThat(normalize(file),
                is(endsWith("/build/classes/java/test/jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest.class")));

        final File file2 = ResourceUtil.getResourceAsFile(
                "jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest", "class");
        assertThat(file2, is(file));
    }

    @Test
    public void test_pathBuilder() throws Throwable {
        final ResourceUtil.Resource resource = ResourceUtil.getResource(
                builder -> builder
                        .append(getClass())
                        .extension("class")
        );

        assertThat(normalize(resource.toPath()),
                is(endsWith("/build/classes/java/test/jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest.class")));
        assertThat(normalize(resource.toFile()),
                is(endsWith("/build/classes/java/test/jp/sourceforge/hotchpotch/coopie/util/ResourceUtilTest.class")));
    }

    private static String normalize(final Path path) {
        if (File.separatorChar == '\\') {
            return path.toString().replace('\\', '/');
        }
        return path.toString();
    }

    private static String normalize(final File path) {
        if (File.separatorChar == '\\') {
            return path.toString().replace('\\', '/');
        }
        return path.toString();
    }

}