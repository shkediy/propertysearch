package com.redislabs.demo.views.propertysearch;

import com.redislabs.demo.data.RedisConnection;
import io.redisearch.AggregationResult;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.aggregation.AggregationBuilder;
import io.redisearch.aggregation.Row;

import java.util.ArrayList;
import java.util.List;

public class PropertySearchEngine {
    private int limit;
    private long offset;
    private long resultSize;
    private RedisConnection conn = new RedisConnection("redis-12000.yuvals.demo.redislabs.com", 12000, "properties");
    //private RedisConnection conn = new RedisConnection("35.227.62.41", 12000, "properties");

    public PropertySearchEngine(int limit) {
        this.limit = limit;
    }

    public long moreResults() {
        return resultSize - offset;
    }

    public List<Property> aggregate(SearchEngineFilter filter) {
        AggregationBuilder r = filter.getAggregation();

        AggregationResult res = conn.getClient().aggregate(r);
        List<Property> results = new ArrayList<Property>();
        for ( int i = 0; i < res.getResults().size(); i++ ) {
            Row row = res.getRow(i);
            if ( row != null ) {
                Property p = new Property("" + i,
                        0,
                        0,
                        row.getDouble(filter.getDBPropertyName(filter.getReduce())),
                        0,
                        0,
                         row.getString(filter.getDBPropertyName(filter.getGroupBy())),
                        "");
                results.add(p);
            }
        }
        return results;
    }

    public List<Property> search(SearchEngineFilter filter, boolean newSearch) {
        if ( newSearch ) {
            offset = 0;
        }

        String query = filter.getQuery();
        Query q = new Query(query);
        q.returnFields("bathroomcnt", "bedroomcnt", "taxvaluedollarcnt",
                        "yearbuilt", "lotsizesquarefeet", "regionidzip", "geopos")
                .limit((int) offset, limit);
        SearchResult res = conn.getClient().search(q);

        resultSize = res.totalResults;
        List<Property> results = new ArrayList<Property>();
        for ( Document d: res.docs ) {
            Property p = new Property(d.getId(),
                    d.get("bathroomcnt") == null ? 0 : Double.valueOf((String)d.get("bathroomcnt")),
                    d.get("bedroomcnt") == null ? 0 :Integer.valueOf((String) d.get("bedroomcnt")),
                    d.get("taxvaluedollarcnt") == null ? 0 : Double.valueOf((String)d.get("taxvaluedollarcnt")),
                    d.get("yearbuilt") == null ? 0 :Integer.valueOf((String) d.get("yearbuilt")),
                    d.get("lotsizesquarefeet") == null ? 0 : Integer.valueOf((String) d.get("lotsizesquarefeet")),
                    d.get("regionidzip") == null  ? "" : d.getString("regionidzip"),
                    d.get("geopos") == null  ? "" : d.getString("geopos"));
            results.add(p);
        }

        offset += res.docs.size();

        return results;

    }



}
