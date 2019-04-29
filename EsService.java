package com.k2data.kbc.kmxes;

import com.k2data.kbc.kmxes.flummi.BoolFilterBuilder;
import com.k2data.kbc.kmxes.flummi.DateHistogramBuilder;
import com.k2data.kbc.kmxes.flummi.DateInterval;
import com.k2data.kbc.kmxes.kmx.KmxObjectService;
import com.k2data.kbc.kmxes.mapper.IndexMapping;
import com.k2data.kbc.kmxes.response.DeviceFileDateCountData;
import com.k2data.kbc.kmxes.util.DateUtils;
import de.otto.flummi.Flummi;
import de.otto.flummi.SortOrder;
import de.otto.flummi.aggregations.TermsBuilder;
import de.otto.flummi.query.BoolQueryBuilder;
import de.otto.flummi.query.DateRangeQueryBuilder;
import de.otto.flummi.query.QueryBuilders;
import de.otto.flummi.request.SearchRequestBuilder;
import de.otto.flummi.response.AggregationResult;
import de.otto.flummi.response.Bucket;
import de.otto.flummi.response.SearchResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EsService {

    /**
     * es查询请求构造器
     */
    private Flummi flummi;
    /**
     * kmx系统的对象服务
     */
    private KmxObjectService kmxObjectService;

    public EsService(Flummi flummi, KmxObjectService kmxObjectService) {
        this.flummi = flummi;
        this.kmxObjectService = kmxObjectService;
    }

    public List<DeviceFileDateCountData> queryDeviceAggrWithoutDay(String dataCategory)
        throws EsException {
        // 1 校验不为空
        if (null == dataCategory) {
            throw new EsException("数据类型不能为空");
        }

        // 2 根据数据类型匹配对应的索引映射信息
        IndexMapping tableIndexMapping = kmxObjectService.queryIndexMapping(dataCategory);
        if (null == tableIndexMapping) {
            throw new EsException("数据类型不存在");
        }

        // 3.2 组织外层查询条件，内层包括device及日期条件
        BoolFilterBuilder outBoolFilterBuilder = new BoolFilterBuilder();
        outBoolFilterBuilder
            .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));

        // 3.3 组织聚合参数
        TermsBuilder aggr = new TermsBuilder("devices");
        aggr.field(tableIndexMapping.getWtField());
        aggr.order("_term", SortOrder.ASC);
        aggr.size(0);

        // 3.3 查询
        SearchRequestBuilder searchRequestBuilder = flummi
            .prepareSearch(tableIndexMapping.getIndex())
            .setQuery(outBoolFilterBuilder.build())
            .addAggregation(aggr);

        SearchResponse searchResponse = searchRequestBuilder.execute();

        // 4 组织返回结果
        List<DeviceFileDateCountData> result = new ArrayList<>();
        AggregationResult devicesAggr = searchResponse.getAggregations().get("devices");
        if (null == devicesAggr) {
            return result;
        }
        for (Bucket deviceBucket : devicesAggr.getBuckets()) {
            DeviceFileDateCountData data = new DeviceFileDateCountData(
                tableIndexMapping.getKmxObjectClassName(),
                deviceBucket.getKey(), null, deviceBucket.getDocCount());

            result.add(data);
        }
        return result;
    }


    public List<DeviceFileDateCountData> queryDeviceAggr(String dataCategory,
        List<String> deviceIdList, List<String> dayList) throws EsException {
        // 1 校验不为空
        if (null == dataCategory) {
            throw new EsException("数据类型不能为空");
        }
        if (null == deviceIdList || deviceIdList.isEmpty() || null == dayList || dayList
            .isEmpty()) {
            throw new EsException("设备或日期不能为空");
        }

        // 2 根据数据类型匹配对应的索引映射信息
        IndexMapping tableIndexMapping = kmxObjectService.queryIndexMapping(dataCategory);
        if (null == tableIndexMapping) {
            throw new EsException("数据类型不存在");
        }

        // 3 构造查询条件并查询
        // 3.1 组织内层日期查询条件
        BoolQueryBuilder innerBoolQueryBuilder = QueryBuilders.bool();
        for (String day : dayList) {
            Date dayDate = DateUtils.parse(day, DateUtils.FORMAT_YYYY_MM_DD);
            OffsetDateTime endDayTime = OffsetDateTime
                .of(DateUtils.getYear(dayDate), DateUtils.getMonth(dayDate),
                    DateUtils.getDate(dayDate), 23, 59, 59, 999000000, ZoneOffset.ofHours(8));
            OffsetDateTime beginDayTime = endDayTime.plusDays(-1);

            DateRangeQueryBuilder dateRangeQueryBuilder = QueryBuilders
                .dateRangeFilter(tableIndexMapping.getTsField());
            dateRangeQueryBuilder.gt(beginDayTime);
            dateRangeQueryBuilder.lte(endDayTime);
            innerBoolQueryBuilder.should(dateRangeQueryBuilder);
        }

        // 3.2 组织外层查询条件，内层包括device及日期条件
        BoolFilterBuilder outBoolFilterBuilder = new BoolFilterBuilder();
        if (null != deviceIdList) {
            outBoolFilterBuilder
                .must(QueryBuilders.termsQuery(tableIndexMapping.getWtField(), deviceIdList))
                .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));
        }
        outBoolFilterBuilder.must(innerBoolQueryBuilder);

        // 3.3 组织聚合参数
        TermsBuilder aggr = new TermsBuilder("devices");
        aggr.field(tableIndexMapping.getWtField());
        aggr.order("_term", SortOrder.ASC);
        aggr.size(0);

        // 3.3 查询
        SearchRequestBuilder searchRequestBuilder = flummi
            .prepareSearch(tableIndexMapping.getIndex())
            .setQuery(outBoolFilterBuilder.build())
            .addAggregation(aggr);

        SearchResponse searchResponse = searchRequestBuilder.execute();

        // 4 组织返回结果
        List<DeviceFileDateCountData> result = new ArrayList<>();
        AggregationResult devicesAggr = searchResponse.getAggregations().get("devices");
        if (null == devicesAggr) {
            return result;
        }
        for (Bucket deviceBucket : devicesAggr.getBuckets()) {
            DeviceFileDateCountData data = new DeviceFileDateCountData(
                tableIndexMapping.getKmxObjectClassName(),
                deviceBucket.getKey(), null, deviceBucket.getDocCount());

            result.add(data);
        }
        return result;
    }

    public List<DeviceFileDateCountData> queryDayAggr(String dataCategory,
        List<String> deviceIdList, List<String> dayList) throws EsException {
        // 1 校验不为空
        if (null == dataCategory) {
            throw new EsException("数据类型不能为空");
        }
        if (null == deviceIdList || deviceIdList.isEmpty() || null == dayList || dayList
            .isEmpty()) {
            throw new EsException("设备或日期不能为空");
        }

        // 2 根据数据类型匹配对应的索引映射信息
        IndexMapping tableIndexMapping = kmxObjectService.queryIndexMapping(dataCategory);
        if (null == tableIndexMapping) {
            throw new EsException("数据类型不存在");
        }

        // 3 构造查询条件并查询
        // 3.1 组织内层日期查询条件
        BoolQueryBuilder innerBoolQueryBuilder = QueryBuilders.bool();
        for (String day : dayList) {
            Date dayDate = DateUtils.parse(day, DateUtils.FORMAT_YYYY_MM_DD);
            OffsetDateTime endDayTime = OffsetDateTime
                .of(DateUtils.getYear(dayDate), DateUtils.getMonth(dayDate),
                    DateUtils.getDate(dayDate), 23, 59, 59, 999000000, ZoneOffset.ofHours(8));
            OffsetDateTime beginDayTime = endDayTime.plusDays(-1);

            DateRangeQueryBuilder dateRangeQueryBuilder = QueryBuilders
                .dateRangeFilter(tableIndexMapping.getTsField());
            dateRangeQueryBuilder.gt(beginDayTime);
            dateRangeQueryBuilder.lte(endDayTime);
            innerBoolQueryBuilder.should(dateRangeQueryBuilder);
        }

        // 3.2 组织外层查询条件，内层包括device及日期条件
        BoolFilterBuilder outBoolFilterBuilder = new BoolFilterBuilder();
        if (null != deviceIdList) {
            outBoolFilterBuilder
                .must(QueryBuilders.termsQuery(tableIndexMapping.getWtField(), deviceIdList))
                .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));
        }
        outBoolFilterBuilder.must(innerBoolQueryBuilder);

        // 3.3 组织聚合参数
        DateHistogramBuilder aggr = new DateHistogramBuilder("days");
        aggr.field(tableIndexMapping.getTsField());
        aggr.interval(DateInterval.DAY);
        aggr.format(DateUtils.FORMAT_YYYY_MM_DD);

        // 3.4 查询
        SearchRequestBuilder searchRequestBuilder = flummi
            .prepareSearch(tableIndexMapping.getIndex())
            .setQuery(outBoolFilterBuilder.build())
            .addAggregation(aggr);

        SearchResponse searchResponse = searchRequestBuilder.execute();

        // 4 组织返回结果
        List<DeviceFileDateCountData> result = new ArrayList<>();
        AggregationResult daysAggr = searchResponse.getAggregations().get("days");
        if (null == daysAggr) {
            return result;
        }
        for (Bucket dayBucket : daysAggr.getBuckets()) {
            if (0 == dayBucket.getDocCount()) {
                continue;
            }
            DeviceFileDateCountData data = new DeviceFileDateCountData(
                tableIndexMapping.getKmxObjectClassName(),
                null, dayBucket.getKey(), dayBucket.getDocCount());

            result.add(data);
        }
        return result;
    }

    public List<DeviceFileDateCountData> queryDeviceAndDayAggr(String dataCategory,
        List<String> deviceIdList,
        List<String> dayList) throws EsException {
        // 1 校验不为空
        if (null == deviceIdList || deviceIdList.isEmpty() || null == dayList || dayList
            .isEmpty()) {
            throw new EsException("设备或日期不能为空");
        }

        // 2 根据数据类型匹配对应的索引映射信息
        IndexMapping tableIndexMapping = kmxObjectService.queryIndexMapping(dataCategory);
        if (null == tableIndexMapping) {
            throw new EsException("数据类型不存在");
        }

        // 3 构造查询条件并查询
        // 3.1 组织内层日期查询条件
        BoolQueryBuilder innerBoolQueryBuilder = QueryBuilders.bool();
        for (String day : dayList) {
            Date dayDate = DateUtils.parse(day, DateUtils.FORMAT_YYYY_MM_DD);
            OffsetDateTime endDayTime = OffsetDateTime
                .of(DateUtils.getYear(dayDate), DateUtils.getMonth(dayDate),
                    DateUtils.getDate(dayDate), 23, 59, 59, 999000000, ZoneOffset.ofHours(8));
            OffsetDateTime beginDayTime = endDayTime.plusDays(-1);

            DateRangeQueryBuilder dateRangeQueryBuilder = QueryBuilders
                .dateRangeFilter(tableIndexMapping.getTsField());
            dateRangeQueryBuilder.gt(beginDayTime);
            dateRangeQueryBuilder.lte(endDayTime);
            innerBoolQueryBuilder.should(dateRangeQueryBuilder);
        }

        // 3.2 组织外层查询条件，内层包括device及日期条件
        BoolFilterBuilder outBoolFilterBuilder = new BoolFilterBuilder();
        if (null != deviceIdList) {
            outBoolFilterBuilder
                .must(QueryBuilders.termsQuery(tableIndexMapping.getWtField(), deviceIdList))
                .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));
        }
        outBoolFilterBuilder.must(innerBoolQueryBuilder);

        // 3.3 组织聚合参数
        TermsBuilder aggr = new TermsBuilder("devices");
        aggr.field(tableIndexMapping.getWtField());
        aggr.order("_term", SortOrder.ASC);
        aggr.size(0);
        DateHistogramBuilder subAggregation = new DateHistogramBuilder("days");
        subAggregation.field(tableIndexMapping.getTsField());
        subAggregation.interval(DateInterval.DAY);
        subAggregation.format(DateUtils.FORMAT_YYYY_MM_DD);
        aggr.subAggregation(subAggregation);

        // 3.4 查询
        SearchRequestBuilder searchRequestBuilder = flummi
            .prepareSearch(tableIndexMapping.getIndex())
            .setQuery(outBoolFilterBuilder.build())
            .addAggregation(aggr);

        SearchResponse searchResponse = searchRequestBuilder.execute();

        // 4 组织返回结果
        List<DeviceFileDateCountData> result = new ArrayList<>();
        AggregationResult devicesAggr = searchResponse.getAggregations().get("devices");
        if (null == devicesAggr) {
            return result;
        }
        for (Bucket deviceBucket : devicesAggr.getBuckets()) {
            AggregationResult daysAggr = deviceBucket.getAggregations().get("days");
            if (null == daysAggr) {
                continue;
            }

            for (Bucket dayBucket : daysAggr.getBuckets()) {
                if (0 == dayBucket.getDocCount()) {
                    continue;
                }
                DeviceFileDateCountData data = new DeviceFileDateCountData(
                    tableIndexMapping.getKmxObjectClassName(),
                    deviceBucket.getKey(), dayBucket.getKey(), dayBucket.getDocCount());

                result.add(data);
            }

        }
        return result;
    }

    public List<DeviceFileDateCountData> stateWtObjectData(String dataCategory,
        List<String> deviceIdList,
        String startTime, String endTime) throws EsException {
        // 1 校验不为空
        if (null == deviceIdList || deviceIdList.isEmpty()) {
            throw new EsException("设备或日期不能为空");
        }

        // 2 根据数据类型匹配对应的索引映射信息
        IndexMapping tableIndexMapping = kmxObjectService.queryIndexMapping(dataCategory);
        if (null == tableIndexMapping) {
            throw new EsException("数据类型不存在");
        }

        // 3 构造查询条件并查询
        // 3.1 组织内层日期查询条件
        BoolQueryBuilder innerBoolQueryBuilder = QueryBuilders.bool();
        Date startDate = DateUtils.parse(startTime, DateUtils.FORMAT_YYYY_MM_DD_HH_M_S);
        if (startDate == null) {
            throw new EsException(String.format("startTime: %s format error.", startTime));
        }
        Date endDate = DateUtils.parse(endTime, DateUtils.FORMAT_YYYY_MM_DD_HH_M_S);
        if (endDate == null) {
            throw new EsException(String.format("endTime: %s format error.", endTime));
        }
        OffsetDateTime beginDayTime = OffsetDateTime
            .of(DateUtils.getYear(startDate), DateUtils.getMonth(startDate),
                DateUtils.getDate(startDate), DateUtils.getHour(startDate),
                DateUtils.getMinute(startDate), DateUtils.getSecond(startDate),
                999000000, ZoneOffset.ofHours(8));
        OffsetDateTime endDayTime = OffsetDateTime
            .of(DateUtils.getYear(endDate), DateUtils.getMonth(endDate),
                DateUtils.getDate(endDate), DateUtils.getHour(endDate),
                DateUtils.getMinute(endDate), DateUtils.getSecond(endDate),
                999000000, ZoneOffset.ofHours(8));

        DateRangeQueryBuilder dateRangeQueryBuilder = QueryBuilders
            .dateRangeFilter(tableIndexMapping.getTsField());
        dateRangeQueryBuilder.gte(beginDayTime);
        dateRangeQueryBuilder.lte(endDayTime);
        innerBoolQueryBuilder.should(dateRangeQueryBuilder);

        // 3.2 组织外层查询条件，内层包括device及日期条件
        BoolFilterBuilder outBoolFilterBuilder = new BoolFilterBuilder();
        if (null != deviceIdList) {
            outBoolFilterBuilder
                .must(QueryBuilders.termsQuery(tableIndexMapping.getWtField(), deviceIdList))
                .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));
        }
        outBoolFilterBuilder.must(innerBoolQueryBuilder);

        // 3.3 组织聚合参数
        TermsBuilder aggr = new TermsBuilder("devices");
        aggr.field(tableIndexMapping.getWtField());
        aggr.order("_term", SortOrder.ASC);
        aggr.size(0);

        // 3.4 查询
        SearchRequestBuilder searchRequestBuilder = flummi
            .prepareSearch(tableIndexMapping.getIndex())
            .setQuery(outBoolFilterBuilder.build())
            .addAggregation(aggr);

        SearchResponse searchResponse = searchRequestBuilder.execute();

        // 4 组织返回结果
        List<DeviceFileDateCountData> result = new ArrayList<>();
        AggregationResult devicesAggr = searchResponse.getAggregations().get("devices");
        if (null == devicesAggr) {
            return result;
        }
        for (Bucket deviceBucket : devicesAggr.getBuckets()) {
            DeviceFileDateCountData data = new DeviceFileDateCountData(
                tableIndexMapping.getKmxObjectClassName(),
                deviceBucket.getKey(), null, deviceBucket.getDocCount());

            result.add(data);

        }
        return result;
    }

    public DeviceFileDateCountData stateObjectData(String dataCategory, List<String> wfList,
        List<String> wtList,
        String startTime, String endTime) throws EsException {
        // 1 校验不为空
        if ((null == wfList || wfList.isEmpty()) && (null == wtList || wtList.isEmpty())) {
            throw new EsException("风场或风机不能为空");
        }

        // 2 根据数据类型匹配对应的索引映射信息
        IndexMapping tableIndexMapping = kmxObjectService.queryIndexMapping(dataCategory);
        if (null == tableIndexMapping) {
            throw new EsException("数据类型不存在");
        }

        // 3 构造查询条件并查询
        // 3.1 组织内层日期查询条件
        BoolQueryBuilder innerBoolQueryBuilder = QueryBuilders.bool();
        Date startDate = DateUtils.parse(startTime, DateUtils.FORMAT_YYYY_MM_DD_HH_M_S);
        if (startDate == null) {
            throw new EsException(String.format("startTime: %s format error.", startTime));
        }
        Date endDate = DateUtils.parse(endTime, DateUtils.FORMAT_YYYY_MM_DD_HH_M_S);
        if (endDate == null) {
            throw new EsException(String.format("endTime: %s format error.", endTime));
        }
        OffsetDateTime beginDayTime = OffsetDateTime
            .of(DateUtils.getYear(startDate), DateUtils.getMonth(startDate),
                DateUtils.getDate(startDate), DateUtils.getHour(startDate),
                DateUtils.getMinute(startDate), DateUtils.getSecond(startDate),
                999000000, ZoneOffset.ofHours(8));
        OffsetDateTime endDayTime = OffsetDateTime
            .of(DateUtils.getYear(endDate), DateUtils.getMonth(endDate),
                DateUtils.getDate(endDate), DateUtils.getHour(endDate),
                DateUtils.getMinute(endDate), DateUtils.getSecond(endDate),
                999000000, ZoneOffset.ofHours(8));

        DateRangeQueryBuilder dateRangeQueryBuilder = QueryBuilders
            .dateRangeFilter(tableIndexMapping.getTsField());
        dateRangeQueryBuilder.gte(beginDayTime);
        dateRangeQueryBuilder.lte(endDayTime);
        innerBoolQueryBuilder.should(dateRangeQueryBuilder);

        // 3.2 组织外层查询条件，内层包括device及日期条件
        BoolFilterBuilder outBoolFilterBuilder = new BoolFilterBuilder();
        if (null != wtList && !wtList.isEmpty()) {
            outBoolFilterBuilder
                .must(QueryBuilders.termsQuery(tableIndexMapping.getWtField(), wtList))
                .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));
        } else {
            outBoolFilterBuilder
                .must(QueryBuilders.termsQuery(tableIndexMapping.getWfField(), wfList))
                .must(QueryBuilders.termQuery(tableIndexMapping.getStatusField(), "1"));
        }
        outBoolFilterBuilder.must(innerBoolQueryBuilder);

        // 3.4 查询
        SearchRequestBuilder searchRequestBuilder = flummi
            .prepareSearch(tableIndexMapping.getIndex())
            .setQuery(outBoolFilterBuilder.build());

        SearchResponse searchResponse = searchRequestBuilder.execute();

        // 4 组织返回结果
        DeviceFileDateCountData data = new DeviceFileDateCountData(
            tableIndexMapping.getKmxObjectClassName(),
            null, null, searchResponse.getHits().getTotalHits());
        return data;
    }
}
