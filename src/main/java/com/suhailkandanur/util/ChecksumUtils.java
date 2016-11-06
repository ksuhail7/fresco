package com.suhailkandanur.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;

/**
 * Created by suhail on 2016-11-04.
 */
public class ChecksumUtils {
    public static String sha1(String str) {
        return DigestUtils.sha1Hex(str);
    }

    public static String sha1(File file) throws FileNotFoundException, IOException {
        return DigestUtils.sha1Hex(new FileInputStream(file));
    }

    public static String sha1(InputStream inputStream) throws IOException {
        return DigestUtils.sha1Hex(inputStream);
    }
}
