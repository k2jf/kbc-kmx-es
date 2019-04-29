package com.k2data.kbc.kmxes.kmx;


import com.k2data.kbc.kmxes.EsException;
import com.k2data.kbc.kmxes.mapper.IndexMapping;

public interface KmxObjectService {

    IndexMapping queryIndexMapping(String objectClassName) throws EsException;
}
