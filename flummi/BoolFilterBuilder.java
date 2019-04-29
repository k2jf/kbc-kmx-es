package com.k2data.kbc.kmxes.flummi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.otto.flummi.query.BoolQueryBuilder;
import de.otto.flummi.query.QueryBuilder;
import de.otto.flummi.request.GsonHelper;

public class BoolFilterBuilder extends BoolQueryBuilder {

    private JsonArray mustFilter = GsonHelper.array(new JsonElement[0]);

    public JsonObject build() {
        if (this.mustFilter.size() == 0) {
            throw new RuntimeException("mustFilter are empty");
        } else {
            JsonObject jsonObject = new JsonObject();
            JsonObject boolObject = new JsonObject();
            jsonObject.add("bool", boolObject);
            if (this.mustFilter.size() > 0) {
                if (this.mustFilter.size() == 1) {
                    boolObject.add("filter", this.mustFilter.get(0));
                } else {
                    boolObject.add("filter", this.mustFilter);
                }
            }

            return jsonObject;
        }
    }

    public boolean isEmpty() {
        return this.mustFilter.size() == 0;
    }

    public BoolFilterBuilder must(JsonObject filter) {
        this.mustFilter.add(filter);
        return this;
    }

    public BoolFilterBuilder must(QueryBuilder queryBuilder) {
        this.must(queryBuilder.build());
        return this;
    }
}
