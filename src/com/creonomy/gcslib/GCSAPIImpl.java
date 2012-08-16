package com.creonomy.gcslib;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import com.creonomy.gcslib.request.GCSContent;
import com.creonomy.gcslib.request.GCSRequest;
import com.creonomy.gcslib.request.GCSRequest.Method;
import com.creonomy.gcslib.request.GCSSigner;

public class GCSAPIImpl implements GCSAPI {

    private String projectId;
    private GCSSigner signer;

    public GCSAPIImpl(String projectId, File certificate) throws Exception {
        this.projectId = projectId;
        this.signer = new GCSSigner(certificate);
    }

    @Override
    public void listBuckets() {

        GCSRequest req = getRequest(Method.GET, "/");

        req.fire();

    }

    @Override
    public void createBucket(String bucket) {

        GCSRequest req = getRequest(Method.PUT, "/");
        req.setBucket(bucket);
        req.setContent(new GCSContent(" "));

        req.fire();

    }

    @Override
    public void insertObject(String bucket, String key, File source, boolean publicRead) {

        GCSRequest req = getRequest(Method.PUT, key);
        req.setBucket(bucket);
        req.setContent(new GCSContent(source));
        if (publicRead)
            req.setHeader("x-goog-acl", "public-read");

        req.fire();

    }

    private GCSRequest getRequest(Method method, String resource) {
        return new GCSRequest(method, resource, projectId, signer);
    }

    public static void closeQuietely(Closeable stream) {
        try {
            if (stream != null)
                stream.close();
        } catch (IOException ignored) {
        }
    }

}