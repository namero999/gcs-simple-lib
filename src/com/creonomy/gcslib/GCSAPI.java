package com.creonomy.gcslib;

import java.io.File;

public interface GCSAPI {

    public void listBuckets();

    public void createBucket(String bucket);

    public void insertObject(String bucket, String key, File source, boolean publicRead);

}