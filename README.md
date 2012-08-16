gcs-simple-lib
==============

A simple Java library to interact with Google Cloud Storage for Developers

Licensed under the I-dont-give-a-shit-what-you-do-with-this-code license ┌∩┐(◕_◕)┌∩┐

Sample Usage:
`
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
`