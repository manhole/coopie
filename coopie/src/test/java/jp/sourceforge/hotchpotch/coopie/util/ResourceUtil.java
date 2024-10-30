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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author manhole
 */
public class ResourceUtil {

    public static Path getResourceAsPath(final String path) throws URISyntaxException {
        final Resource resource = getResource(path, null);
        if (resource == null) {
            return null;
        }
        return resource.toPath();
    }

    public static File getResourceAsFile(final String path) {
        final Resource resource = getResource(path, null);
        if (resource == null) {
            return null;
        }
        return resource.toFile();
    }

    public static File getResourceAsFile(final String path, final String extension) {
        final Resource resource = getResource(path, extension);
        if (resource == null) {
            return null;
        }
        return resource.toFile();
    }

    public static InputStream getResourceAsStream(final String path, final String extension) {
        final Resource resource = getResource(path, extension);
        if (resource == null) {
            return null;
        }
        return resource.openStream();
    }

    private static Resource getResource(final String path, final String extension) {
        final ResourcePath resourcePath = ResourcePath.create(path, extension);
        return getResource(resourcePath);
    }

    private static Resource getResource(final ResourcePath resourcePath) {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (resourcePath == null || loader == null) {
            return null;
        }
        final URL url = loader.getResource(resourcePath.toPath());
        if (url == null) {
            return null;
        }
        return new Resource(url);
    }

    private static File toFile(final URL url) {
        final String s = url.getFile();
        final String fileName = decodeUrl(s);
        return new File(fileName);
    }

    private static String decodeUrl(final String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static File getBuildDir(final Class<?> clazz) {
        final ResourcePath resourcePath = ResourcePath.create(clazz);
        final Resource resource = getResource(resourcePath);
        if (resource == null) {
            return null;
        }
        final URL url = resource.getUrl();
        final String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            final int num = resourcePath.toPath().split("/").length;
            File dir = toFile(url);
            for (int i = 0; i < num; ++i, dir = dir.getParentFile()) {
            }
            return dir;
        } else {
            return null;
        }
    }

    public static Resource getResource(final Consumer<ResourcePathBuilder> o) {
        final ResourcePathBuilder builder = new ResourcePathBuilder();
        o.accept(builder);
        final ResourcePath resourcePath = builder.build();
        return getResource(resourcePath);
    }

    public static class ResourcePathBuilder {

        private final List<String> components_ = new ArrayList<>();

        private String extension_;

        public ResourcePathBuilder append(final Class<?> clazz) {
            Collections.addAll(components_, clazz.getName().split("\\."));
            return this;
        }

        public ResourcePathBuilder append(final Package pkg) {
            Collections.addAll(components_, pkg.getName().split("\\."));
            return this;
        }

        public ResourcePathBuilder append(final String component) {
            components_.add(component);
            return this;
        }

        public ResourcePathBuilder editLast(final UnaryOperator<String> editor) {
            final String component = components_.remove(components_.size() - 1);
            final String result = editor.apply(component);
            components_.add(result);
            return this;
        }

        public ResourcePathBuilder extension(final String extension) {
            extension_ = extension;
            return this;
        }

        ResourcePath build() {
            final String path = String.join("/", components_);
            return ResourcePath.create(path, extension_);
        }

    }

    private static class ResourcePath {

        private final String path_;

        protected ResourcePath(final String path, final String extension) {
            if (extension == null) {
                path_ = path;
            } else {
                if (extension.startsWith(".")) {
                    path_ = path + extension;
                } else {
                    path_ = path + "." + extension;
                }
            }
        }

        public String toPath() {
            return path_;
        }

        public static ResourcePath create(final String path, final String extension) {
            return new ResourcePath(path, extension);
        }

        public static ResourcePath create(final Class<?> clazz) {
            return new ResourcePath(clazz.getName(), "class");
        }

    }

    public static class Resource {

        private final URL url_;

        protected Resource(final URL url) {
            url_ = url;
        }

        public URL getUrl() {
            return url_;
        }

        public Path toPath() throws URISyntaxException {
            return Paths.get(url_.toURI());
        }

        public File toFile() {
            return ResourceUtil.toFile(url_);
        }

        public InputStream openStream() {
            return openStream(url_);
        }

        private static InputStream openStream(final URL url) {
            try {
                final URLConnection con = url.openConnection();
                con.setUseCaches(false);
                return con.getInputStream();
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        }

    }

}
