package com.k2data.kbc.kmxes;


import com.k2data.kbc.kmxes.config.Environment;
import com.k2data.kbc.kmxes.kmx.KmxObjectService;
import com.k2data.kbc.kmxes.kmx.KmxObjectServiceImpl;

public class EsServiceBuilder {

    public EsService build(String kmxUrl, String kmxCasUrl, String kmxToken, String esUrl) {
        Environment environment = new Environment(kmxUrl, kmxCasUrl, kmxToken, esUrl);

        KmxObjectServiceImpl kmxObjectService = new KmxObjectServiceImpl(
            environment.getKmxRestClient());

        return new EsService(environment.getEsFlummi(), kmxObjectService);
    }

    public EsService build(Environment environment) {
        KmxObjectServiceImpl kmxObjectService = new KmxObjectServiceImpl(
            environment.getKmxRestClient());

        return new EsService(environment.getEsFlummi(), kmxObjectService);
    }

    public EsService build(String esUrl, KmxObjectService kmxObjectService) {
        Environment environment = new Environment(null, null, null, esUrl);

        return new EsService(environment.getEsFlummi(), kmxObjectService);
    }
}
