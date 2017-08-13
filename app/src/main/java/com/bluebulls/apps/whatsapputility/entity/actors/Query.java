package com.bluebulls.apps.whatsapputility.entity.actors;

/**
 * Created by ogil on 10/08/17.
 */

public class Query {

    private String query;
    private String baseQuery;

    public Query() {}

    public Query(String query, String baseQuery){
        this.query = query;
        this.baseQuery = baseQuery;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getBaseQuery() {
        return baseQuery;
    }

    public void setBaseQuery(String baseQuery) {
        this.baseQuery = baseQuery;
    }
}
