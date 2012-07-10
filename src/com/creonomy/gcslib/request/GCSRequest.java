package com.creonomy.gcslib.request;

import static com.creonomy.gcslib.GCSAPIImpl.closeQuietely;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import com.creonomy.gcslib.Constants;
import com.creonomy.gcslib.GCSException;

public class GCSRequest {

    private Method method;
    private String resource;
    private String projectId;

    private String bucket;

    private GCSContent content;

    private static final long DEFAULT_VALIDITY = 30; // seconds
    private long validity = DEFAULT_VALIDITY;

    private long expires;

    private GCSSigner signer;

    private static final DateFormat df = new SimpleDateFormat(Constants.dateFormat, Locale.US);
    static {
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public GCSRequest(Method method, String resource, String projectId, GCSSigner signer) {
        this.method = method;
        this.resource = resource;
        this.projectId = projectId;
        this.signer = signer;
    }

    public void fire() throws GCSException {

        try {

            Date now = new Date();
            expires = now.getTime() / 1000 + validity;

            addDefaultHeaders(now);
            addContentHeaders();

            String url = signer.sign(this);

            HttpsURLConnection connection = getConnection(url);
            connection.connect();

            if (connection.getDoOutput())
                writeContent(connection);

            handle(connection);
            connection.disconnect();

        } catch (Exception e) {
            throw new GCSException("An error occurred", e);
        }

    }

    private void writeContent(HttpsURLConnection connection) throws IOException {

        InputStream is = null;
        OutputStream os = null;
        try {
            is = content.getInputStream();
            os = connection.getOutputStream();
            copy(is, os);
        } finally {
            closeQuietely(is);
            closeQuietely(os);
        }

    }

    private static void copy(InputStream is, OutputStream os) throws IOException {

        byte[] buffer = new byte[10240];

        int count = 0;
        while ((count = is.read(buffer)) != -1)
            os.write(buffer, 0, count);

        os.flush();

    }

    private void handle(HttpsURLConnection connection) throws Exception {

        int rc = connection.getResponseCode();
        System.out.println(rc + " " + connection.getResponseMessage());

        BufferedReader in = null;
        try {

            InputStream stream = (rc >= 200 && rc < 300) ? connection.getInputStream() : connection.getErrorStream();

            in = new BufferedReader(new InputStreamReader(stream));

            String line = null;
            while ((line = in.readLine()) != null)
                System.out.println(line);

        } finally {
            closeQuietely(in);
        }

    }

    private void addDefaultHeaders(Date now) {

        String host = bucket == null ? "" : bucket + '.';

        setHeader("Host", host + Constants.rootHost);
        setHeader("Date", df.format(now));
        setHeader("x-goog-api-version", Constants.apiVersion);
        setHeader("x-goog-project-id", projectId);

    }

    private void addContentHeaders() {

        if (content != null) {
            setHeader("Content-Length", content.getLength());
            setHeader("Content-Type", content.getType());
            setHeader("Content-MD5", content.getBase64MD5());
        } else {
            setHeader("Content-Length", 0);
        }

    }

    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> headersReadOnly = Collections.unmodifiableMap(headers);

    public Map<String, String> getHeaders() {
        return headersReadOnly;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public void setHeader(String header, Object value) {
        if (value != null)
            headers.put(header, value.toString());
        else
            headers.remove(header);
    }

    private HttpsURLConnection getConnection(String url) throws Exception {

        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method.toString());
        connection.setUseCaches(false);

        for (String key : headers.keySet())
            connection.setRequestProperty(key, headers.get(key));

        connection.setDoOutput(content != null);

        return connection;

    }

    public Method getMethod() {
        return method;
    }

    public String getResource() {
        return resource;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public boolean hasBucket() {
        return bucket != null;
    }

    public GCSContent getContent() {
        return content;
    }

    public void setContent(GCSContent content) {
        this.content = content;
    }

    public long getValidity() {
        return validity;
    }

    public void setValidity(long validity) {
        this.validity = validity;
    }

    public long getExpires() {
        return expires;
    }

    public enum Method {

        GET,
        PUT,
        DELETE,
        HEAD;

    }

}