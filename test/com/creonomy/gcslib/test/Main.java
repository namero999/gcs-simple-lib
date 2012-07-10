package com.creonomy.gcslib.test;

import java.io.File;

import com.creonomy.gcslib.GCSAPI;
import com.creonomy.gcslib.GCSAPIImpl;

public class Main {

    private static String projectId = "Yout project ID";

    private static String certificatePath = "Your .p12 certificate path";

    public static void main(String[] args) throws Exception {

        GCSAPI api = new GCSAPIImpl(projectId, certificatePath);

        api.listBuckets();
        api.createBucket("bucket999");
        api.insertObject("bucket999", "/test.jpg", new File("test.jpg"), true);

    }

}