package com.k2data.kbc.kmxes.config;

import com.k2data.kbc.kmxes.kmx.KmxRestClient;
import de.otto.flummi.Flummi;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

public class Environment {

    private Flummi esFlummi;
    private KmxRestClient kmxRestClient;

    public Environment(String kmxUrl, String kmxCasUrl, String kmxToken, String esUrl) {
        this.kmxRestClient = new KmxRestClient(kmxUrl, kmxCasUrl, kmxToken);
        this.esFlummi = createFlummi(esUrl);
    }

    public Flummi createFlummi(String url) {
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient();
        return new Flummi(asyncHttpClient, url);
    }

    public Flummi getEsFlummi() {
        return esFlummi;
    }

    public KmxRestClient getKmxRestClient() {
        return kmxRestClient;
    }
}
