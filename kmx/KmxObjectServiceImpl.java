package com.k2data.kbc.kmxes.kmx;

import com.k2data.kbc.kmxes.EsException;
import com.k2data.kbc.kmxes.kmx.convert.ConvertObjectClassToIndexMapping;
import com.k2data.kbc.kmxes.kmx.model.KmxObjectClass;
import com.k2data.kbc.kmxes.kmx.model.KmxResponse;
import com.k2data.kbc.kmxes.kmx.model.ObjectClassesData;
import com.k2data.kbc.kmxes.mapper.IndexMapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KmxObjectServiceImpl implements KmxObjectService {

    private KmxRestClient kmxRestClient;
    private Map<String, IndexMapping> indexMappingCache;

    public KmxObjectServiceImpl(KmxRestClient kmxRestClient) {
        this.kmxRestClient = kmxRestClient;
        this.indexMappingCache = new HashMap();
    }

    public List<IndexMapping> queryIndexMappings() throws EsException {
        List<IndexMapping> result = new ArrayList<>();

        KmxResponse<ObjectClassesData> classesResponse = null;
        try {
            classesResponse = kmxRestClient.get("/object-classes", ObjectClassesData.class);
        } catch (Exception e) {
            throw new EsException(e.getMessage());
        }
        if (!classesResponse.isSuccessed()) {
            throw new EsException(classesResponse.getMessage());
        }

        List<KmxObjectClass> objectClasses = classesResponse.getData().getObjectClasses();
        if (null == objectClasses) {
            return result;
        }

        for (KmxObjectClass objectClass : objectClasses) {
            IndexMapping objectIndexMapping = new ConvertObjectClassToIndexMapping()
                .convert(objectClass);
            result.add(objectIndexMapping);
        }

        return result;
    }

    @Override
    public IndexMapping queryIndexMapping(String objectClassName) throws EsException {
        IndexMapping result = indexMappingCache.get(objectClassName);
        if (null != result) {
            return result;
        }

        freshCache();
        return indexMappingCache.get(objectClassName);
    }

    private void freshCache() throws EsException {
        Map<String, IndexMapping> newIndexMappingCache = new HashMap<>();
        List<IndexMapping> indexMappings = queryIndexMappings();
        for (IndexMapping indexMapping : indexMappings) {
            newIndexMappingCache.put(indexMapping.getKmxObjectClassName(), indexMapping);
        }

        indexMappingCache = newIndexMappingCache;
    }
}
