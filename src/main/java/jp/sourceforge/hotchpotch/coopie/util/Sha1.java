package jp.sourceforge.hotchpotch.coopie.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha1 {

    private final FileOperation files_ = new FileOperation();

    public String digest(final File file) throws IOException {
        final MessageDigest md = getMessageDigest();
        final BufferedInputStream is = files_.openBufferedInputStream(file);
        try {
            final byte[] bytes = new byte[files_.getBufferSize()];
            while (true) {
                final int len = is.read(bytes);
                if (len == -1) {
                    break;
                }
                md.update(bytes, 0, len);
            }
        } finally {
            IOUtil.closeNoException(is);
        }

        final byte[] digest = md.digest();
        return toHexString(digest);
    }

    private MessageDigest getMessageDigest() {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            return md;
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String toHexString(final byte[] digest) {
        final StringBuilder sb = new StringBuilder();
        for (final byte b : digest) {
            final String s = toHexString(b);
            sb.append(s);
        }
        final String s = sb.toString();
        return s;
    }

    private String toHexString(final byte b) {
        final int i = byteToInt(b);
        final String s = Integer.toHexString(i);
        if (s.length() == 1) {
            return "0" + s;
        }
        return s;
    }

    private int byteToInt(final byte b) {
        return b & 0xFF;
    }

}
