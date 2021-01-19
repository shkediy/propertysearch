package com.redislabs.demo.views.addresssearch;

import com.redislabs.demo.data.RedisConnection;
import io.redisearch.Document;
import io.redisearch.Query;
import io.redisearch.SearchResult;
import io.redisearch.Suggestion;
import io.redisearch.client.SuggestionOptions;

import java.util.ArrayList;
import java.util.List;

public class AddressSearchEngine {
    private int limit;
    private long offset;
    private long resultSize;
    private RedisConnection conn = new RedisConnection("redis-12000.yuvals.demo.redislabs.com", 12000, "addresses");
    //private RedisConnection conn = new RedisConnection("35.227.62.41", 12000, "addresses");

    public AddressSearchEngine(int limit) {
        this.limit = limit;
    }

    public long moreResults() {
        return resultSize - offset;
    }

    private String getValue(Document doc, String column) {
        return doc.get(column) == null ? "" : doc.getString(column);
    }

    public List<Address> search(SearchEngineFilter filter, boolean newSearch) {
        if ( newSearch ) {
            offset = 0;
        }

        String query = filter.getQuery();
        Query q = new Query(query);

        q.returnFields("NUMBER", "STREET", "UNIT",
                        "CITY", "POSTCODE", "STATE", "GEOPOS")
                .limit((int) offset, limit);

        if ( filter.getHighlight() )
            q.highlightFields(new Query.HighlightTags("<mark>", "</mark>"), "STREET", "CITY");

        q.setSortBy(filter.getSort(), filter.isAscending());



        SearchResult res = conn.getClient().search(q);
        resultSize = res.totalResults;
        List<Address> results = new ArrayList<Address>();
        for ( Document d: res.docs ) {
            Address a = new Address(getValue(d, "NUMBER"),
                    getValue(d, "STREET"),
                    getValue(d, "UNIT"),
                    getValue(d, "CITY"),
                    getValue(d, "POSTCODE"),
                    getValue(d, "STATE"),
                    getValue(d, "GEOPOS"));
            results.add(a);
        }

        offset += res.docs.size();

        return results;

    }

    public List<String> getSuggestions(String prefix, int max) {
        ;
        List<Suggestion> res =  conn.getClient().getSuggestion(prefix, SuggestionOptions.builder().max(max).build());
        List<String> sugs = new ArrayList<String>();
        for (Suggestion s: res) {
            sugs.add(s.getString());
        }
        return sugs;
    }

    private void loadSuggestions(String column) {
        Query q = new Query("*");
        q.returnFields(column);
        q.limit(0, 10000);
        SearchResult res = conn.getClient().search(q);
        for ( Document d: res.docs ) {
            if ( d.get(column) != null )
                conn.getClient().addSuggestion(Suggestion.builder().str(d.getString(column)).score(1).build(), false);
        }
    }

}
