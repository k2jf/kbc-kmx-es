package com.k2data.kbc.kmxes.kmx;

import com.alibaba.fastjson.JSON;
import com.k2data.kbc.kmxes.kmx.model.KmxResponse;
import com.k2data.platform.common.utils.cas.CasHttpClient;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

public class KmxRestClient {

    private static final String CAS_TOKEN_KEY = "K2_KEY";
    private String kmxUrl;
    private String casUrl;
    private String token;

    public KmxRestClient(String kmxUrl, String casUrl, String token) {
        this.kmxUrl = kmxUrl;
        this.casUrl = casUrl;
        this.token = token;
    }

    public <T> KmxResponse<T> get(String path, Map<String, Object> params, Class<T> cls)
        throws Exception {
        HttpGet kmxGet = new HttpGet(addParamsToUrl(this.kmxUrl + path, params));
        kmxGet.addHeader(CAS_TOKEN_KEY, token);
        CasHttpClient casHttpClient = new CasHttpClient(casUrl);
        HttpResponse kmxResponse = casHttpClient.doGet(kmxGet);

        String kmxResponseStr = EntityUtils.toString(kmxResponse.getEntity());
        KmxResponse<T> result = JSON
            .parseObject(kmxResponseStr, KmxResponse.class);
        if (kmxResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            result.faild();
            return result;
        }

        T data = JSON
            .parseObject(kmxResponseStr, cls);
        result.setData(data);
        return result;
    }

    public <T> KmxResponse<T> get(String path, Class<T> cls) throws Exception {
        return get(path, null, cls);
    }

    private String addParamsToUrl(String url, Map<String, Object> params) {
        if (null == params) {
            return url;
        }

        for (Entry<String, Object> param : params.entrySet()) {
            if (url.contains("?")) {
                url = url + "&" + param.getKey() + "=" + param.getValue();
            } else {
                url = url + "?" + param.getKey() + "=" + param.getValue();
            }
        }
        return url;
    }
}
