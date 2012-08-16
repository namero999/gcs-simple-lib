package com.creonomy.gcslib.test;

import java.io.File;

import com.creonomy.gcslib.GCSAPI;
import com.creonomy.gcslib.GCSAPIImpl;

public class Main {

    private static String projectId = "36724130837";

    private static String certificatePath = "12e26dc97695bdf06a36b36034767110e8f917fa-privatekey.p12";

    public static void main(String[] args) throws Exception {

        GCSAPI api = new GCSAPIImpl(projectId, new File(certificatePath));

        api.listBuckets();
        api.createBucket("bucket999");
        api.insertObject("bucket999", "/test.jpg", new File("test.jpg"), true);

    }

}