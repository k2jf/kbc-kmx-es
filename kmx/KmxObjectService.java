package com.k2data.kbc.kmx.es.kmx;


import com.k2data.kbc.kmx.es.EsException;
import com.k2data.kbc.kmx.es.mapper.IndexMapping;

public interface KmxObjectService {

    IndexMapping queryIndexMapping(String objectClassName) throws EsException;
}
