package com.creonomy.gcslib.request;

import static com.creonomy.gcslib.GCSAPIImpl.closeQuietely;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import com.creonomy.gcslib.GCSException;

public class GCSContent {

    private Object content;

    private long length;
    private String type;
    private String base64MD5;

    private static final MimetypesFileTypeMap mime = new MimetypesFileTypeMap();

    public GCSContent(Object content) throws GCSException {

        if (!(content instanceof String || content instanceof File))
            throw new GCSException("Content type not supported");

        this.content = content;

        try {
            digestContent();
        } catch (Exception e) {
            throw new GCSException("Content not readable", e);
        }

    }

    public long getLength() {
        return length;
    }

    public String getType() {
        return type;
    }

    public String getBase64MD5() {
        return base64MD5;
    }

    private void digestContent() throws Exception {

        if (content instanceof String) {

            String string = (String) content;

            base64MD5 = Base64.encodeBase64String(DigestUtils.md5(string));
            length = string.length();
            type = null;

        } else if (content instanceof File) {

            File file = (File) content;

            FileInputStream fis = null;
            try {

                fis = new FileInputStream(file);

                base64MD5 = Base64.encodeBase64String(DigestUtils.md5(fis));
                length = file.length();
                type = mime.getContentType(file);
                System.out.println("Detected: " + type);

            } finally {
                closeQuietely(fis);
            }

        }

    }

    public InputStream getInputStream() throws FileNotFoundException {

        if (content instanceof String) {

            String string = (String) content;

            return new ByteArrayInputStream(string.getBytes());

        } else if (content instanceof File) {

            File file = (File) content;

            try {

                return new FileInputStream(file);

            } catch (IOException e) {
                throw new GCSException("Content not readable", e);
            }

        }

        return null;

    }

}