package com.k2data.kbc.kmx.es.kmx.convert;

import com.k2data.kbc.kmx.es.kmx.model.KmxObjectClass;
import com.k2data.kbc.kmx.es.kmx.model.KmxObjectColumn;
import com.k2data.kbc.kmx.es.mapper.IndexMapping;
import com.k2data.kbc.kmx.es.util.StringUtils;
import java.util.List;

public class ConvertObjectClassToIndexMapping {

    public IndexMapping convert(KmxObjectClass kmxObjectClass) {
        IndexMapping result = new IndexMapping();

        result.setKmxObjectClassName(kmxObjectClass.getObjectClassName());
        result.setIndex("object_metadata_" + kmxObjectClass.getObjectClassId() + "_default");

        KmxObjectColumn wtColumn = getColumn("wtid", kmxObjectClass.getObjectColumnInfoList());
        if (null != wtColumn) {
            result.setWtField("f_" + wtColumn.getId());
        }

        KmxObjectColumn wfColumn = getColumn("wfid", kmxObjectClass.getObjectColumnInfoList());
        if (null != wfColumn) {
            result.setWfField("f_" + wfColumn.getId());
        }

        KmxObjectColumn tsColumn = getColumn("ts", kmxObjectClass.getObjectColumnInfoList());
        if (null != tsColumn) {
            result.setTsField("f_" + tsColumn.getId());
        }

        return result;
    }

    private KmxObjectColumn getColumn(String fieldName, List<KmxObjectColumn> columns) {
        if (StringUtils.isEmpty(fieldName) || null == columns) {
            return null;
        }
        for (KmxObjectColumn column : columns) {
            if (StringUtils.equals(fieldName, column.getFieldName())) {
                return column;
            }
        }
        return null;
    }
}
