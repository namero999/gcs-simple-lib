gcs-simple-lib
==============

A simple(r) Java library to interact with Google Cloud Storage for Developers

Licensed under the Do-what-you-want-with-this-code-but-don-t-blame-me-if-the-universe-implode License.

Sample Usage:

    private static String projectId = "your-project-id";

    private static String certificatePath = "your-p12-certificate";

    public static void main(String[] args) throws Exception {

        GCSAPI api = new GCSAPIImpl(projectId, new File(certificatePath));

        api.listBuckets();
        api.createBucket("bucket999");
        api.insertObject("bucket999", "/test.jpg", new File("test.jpg"), true);

    }