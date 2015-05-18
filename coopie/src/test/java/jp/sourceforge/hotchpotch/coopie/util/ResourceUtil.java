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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

/**
 * @author manhole
 */
public class ResourceUtil {

    public static File getResourceAsFile(final String path) {
        final Resource resource = getResource(path, null);
        if (resource == null) {
            return null;
        }
        return resource.asFile();
    }

    public static File getResourceAsFile(final String path, final String extension) {
        final Resource resource = getResource(path, extension);
        if (resource == null) {
            return null;
        }
        return resource.asFile();
    }

    public static InputStream getResourceAsStream(final String path, final String extension) {
        final Resource resource = getResource(path, extension);
        if (resource == null) {
            return null;
        }
        return resource.asStream();
    }

    private static Resource getResource(final String path, final String extension) {
        final ResourcePath resourcePath = ResourcePath.create(path, extension);
        final Resource resource = getResource(resourcePath);
        return resource;
    }

    private static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private static Resource getResource(final ResourcePath resourcePath) {
        final ClassLoader loader = getContextClassLoader();
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
        if (url == null) {
            return null;
        }

        final String fileName = getFileName(url);
        final File file = new File(fileName);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    private static String getFileName(final URL url) {
        final String s = url.getFile();
        return decodeUrl(s);
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
            File dir = new File(getFileName(url));
            for (int i = 0; i < num; ++i, dir = dir.getParentFile()) {
            }
            return dir;
        } else {
            return null;
        }
    }

    private static String decodeUrl(final String s) {
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream openStream(final URL url) {
        try {
            final URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (final IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private static String replace(final String s) {
        return s.replace('.', '/');
    }

    private static class ResourcePath {

        private final String _path;

        protected ResourcePath(final String path, final String extension) {
            if (extension == null) {
                _path = path;
            } else {
                final String prefix = replace(path);
                if (extension.startsWith(".")) {
                    _path = prefix + extension;
                } else {
                    _path = prefix + "." + extension;
                }
            }
        }

        public String toPath() {
            return _path;
        }

        public static ResourcePath create(final String path, final String extension) {
            return new ResourcePath(path, extension);
        }

        public static ResourcePath create(final Class<?> clazz) {
            return new ResourcePath(clazz.getName(), "class");
        }

    }

    private static class Resource {

        private final URL _url;

        protected Resource(final URL url) {
            _url = url;
        }

        public URL getUrl() {
            return _url;
        }

        public File asFile() {
            return toFile(_url);
        }

        public InputStream asStream() {
            return openStream(_url);
        }

    }

}
