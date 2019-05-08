package com.k2data.kbc.kmx.es.flummi;

import static de.otto.flummi.request.GsonHelper.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import de.otto.flummi.aggregations.SubAggregationBuilder;
import de.otto.flummi.response.AggregationResult;
import de.otto.flummi.response.Bucket;
import de.otto.flummi.response.BucketAggregationResult;
import java.util.ArrayList;

public class DateHistogramBuilder extends SubAggregationBuilder<DateHistogramBuilder> {

    private static final String BEIJING_ZONE = "+08:00";
    private String fieldName;
    private DateInterval interval;
    private String format;
    private String timeZone;

    public DateHistogramBuilder(String name) {
        super(name);
        this.timeZone = BEIJING_ZONE;
    }

    @Override
    public JsonObject build() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("field", new JsonPrimitive(fieldName));
        if (null != interval) {
            jsonObject.add("interval", new JsonPrimitive(interval.getName()));
        }
        if (null != format && !format.isEmpty()) {
            jsonObject.add("format", new JsonPrimitive(format));
        }
        jsonObject.add("time_zone", new JsonPrimitive(timeZone));

        return object("date_histogram", jsonObject);
    }

    @Override
    public AggregationResult parseResponse(JsonObject jsonObject) {
        AggregationResult aggregation = null;

        JsonElement bucketsElement = jsonObject.get("buckets");
        if (bucketsElement != null) {
            JsonArray bucketsArray = bucketsElement.getAsJsonArray();
            ArrayList<Bucket> bucketList = new ArrayList<>();
            for (JsonElement elem : bucketsArray) {
                JsonObject elemObject = elem.getAsJsonObject();
                bucketList.add(new Bucket(elemObject.get("key_as_string").getAsString(),
                    elemObject.get("doc_count").getAsLong()));
            }
            aggregation = new BucketAggregationResult(bucketList);
        }
        return aggregation;
    }

    public DateHistogramBuilder field(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public DateHistogramBuilder interval(DateInterval interval) {
        this.interval = interval;
        return this;
    }

    public DateHistogramBuilder format(String format) {
        this.format = format;
        return this;
    }

    public DateHistogramBuilder timeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }
}

