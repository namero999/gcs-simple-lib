gcs-simple-lib
==============

A simple Java library to interact with Google Cloud Storage for Developers

Licensed under the I-dont-give-a-shit-what-you-do-with-this-code license ┌∩┐(◕_◕)┌∩┐

Sample Usage:

    private static String projectId = "your-project-id";

    private static String certificatePath = "your-p12-certificate";

    public static void main(String[] args) throws Exception {

        GCSAPI api = new GCSAPIImpl(projectId, new File(certificatePath));

        api.listBuckets();
        api.createBucket("bucket999");
        api.insertObject("bucket999", "/test.jpg", new File("test.jpg"), true);

    }