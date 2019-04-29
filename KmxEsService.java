package com.k2data.kbc.kmxes;

import com.k2data.kbc.kmxes.response.DeviceFileDateCountData;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KmxEsService {

    @Value("${kbc.kmxes.kmx.url}")
    private String kmxUrl;

    @Value("${kbc.kmxes.kmx.casurl}")
    private String kmxCasUrl;

    @Value("${kbc.kmxes.kmx.token}")
    private String kmxToken;

    @Value("${kbc.kmxes.url}")
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
