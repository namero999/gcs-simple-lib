package com.creonomy.gcslib.request;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.creonomy.gcslib.Constants;
import com.creonomy.gcslib.GCSException;

public class GCSSigner {

    private static final char S = '\n';

    private static final char[] password = "notasecret".toCharArray();

    private Signature signer;

    public GCSSigner(String certificatePath) throws GCSException {

        try {

            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(certificatePath), password);
            PrivateKey key = (PrivateKey) ks.getKey("privatekey", password);

            signer = Signature.getInstance("SHA256withRSA");
            signer.initSign(key);

        } catch (Exception e) {
            throw new GCSException("Signer creation failed", e);
        }

    }

    public String sign(GCSRequest req) throws GCSException {

        try {

            String signedRequest = signRequest(req);
            signedRequest = URLEncoder.encode(signedRequest, "UTF-8");

            return buildUrl(req, signedRequest);

        } catch (Exception e) {
            throw new GCSException("Error while signing request", e);
        }
    }

    private String buildUrl(GCSRequest req, String signedRequest) {

        String accessId = req.getProjectId() + Constants.accessDomain;

        StringBuilder sb = new StringBuilder("https://");

        if (req.hasBucket())
            sb.append(req.getBucket()).append("."); // https://bucket.

        sb.append(Constants.rootHost).append(req.getResource()); // https://bucket.commondatastorage.googleapis.com/

        sb.append("?GoogleAccessId=").append(accessId);
        sb.append("&Expires=").append(req.getExpires());
        sb.append("&Signature=").append(signedRequest);

        return sb.toString();

    }

    private String signRequest(GCSRequest req) throws Exception {

        String md5 = req.getHeader("Content-MD5");
        md5 = md5 == null ? "" : md5;
        String type = req.getHeader("Content-Type");
        type = type == null ? "" : type;

        StringBuilder sb = new StringBuilder();

        // HEADERS

        sb.append(req.getMethod()).append(S);
        sb.append(md5).append(S);
        sb.append(type).append(S);
        sb.append(req.getExpires()).append(S);

        // EXTENSIONS

        List<String> list = getXGoogHeaders(req);
        Collections.sort(list);

        for (String extension : list)
            sb.append(extension).append(S);

        // RESOURCE

        if (req.hasBucket())
            sb.append("/").append(req.getBucket());

        sb.append(req.getResource());

        signer.update(sb.toString().getBytes());
        return Base64.encodeBase64String(signer.sign());

    }

    private List<String> getXGoogHeaders(GCSRequest req) {

        List<String> list = new ArrayList<String>();

        Map<String, String> headers = req.getHeaders();
        for (String key : headers.keySet())
            if (key.startsWith("x-goog-"))
                list.add(key + ":" + headers.get(key));

        return list;

    }

}