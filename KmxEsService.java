package com.k2data.kbc.kmx.es;

import com.k2data.kbc.kmx.es.response.DeviceFileDateCountData;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KmxEsService {

    @Value("${kbc.kmx.es.kmx.url}")
    private String kmxUrl;

    @Value("${kbc.kmx.es.kmx.casurl}")
    private String kmxCasUrl;

    @Value("${kbc.kmx.es.kmx.token}")
    private String kmxToken;

    @Value("${kbc.kmx.es.url}")
    private String esUrl;

    private EsService esService;

    @PostConstruct
    private void postConstruct() {
        this.esService = new EsServiceBuilder().build(this.kmxUrl, this.kmxCasUrl, this.kmxToken, this.esUrl);
    }

    public List<DeviceFileDateCountData> queryDeviceAggrWithoutDay(String dataCategory)
        throws EsException {
        return this.esService.queryDeviceAggrWithoutDay(dataCategory);
    }

    public List<DeviceFileDateCountData> queryDeviceAggr(String dataCategory,
        List<String> deviceIdList, List<String> dayList) throws EsException {
        return this.queryDeviceAggr(dataCategory, deviceIdList, dayList);
    }

    public List<DeviceFileDateCountData> queryDayAggr(String dataCategory,
        List<String> deviceIdList, List<String> dayList) throws EsException {
        return this.queryDayAggr(dataCategory, deviceIdList, dayList);
    }

    public List<DeviceFileDateCountData> queryDeviceAndDayAggr(String dataCategory,
        List<String> deviceIdList,
        List<String> dayList) throws EsException {
        return this.queryDeviceAndDayAggr(dataCategory, deviceIdList, dayList);
    }

    public List<DeviceFileDateCountData> stateWtObjectData(String dataCategory,
        List<String> deviceIdList,
        String startTime, String endTime) throws EsException {
        return this.esService.stateWtObjectData(dataCategory, deviceIdList, startTime, endTime);
    }

    public DeviceFileDateCountData stateObjectData(String dataCategory, List<String> wfList,
        List<String> wtList,
        String startTime, String endTime) throws EsException {
        return this.stateObjectData(dataCategory, wfList, wtList, startTime, endTime);
    }
}
