package com.redislabs.demo.views.propertysearch;

import io.redisearch.aggregation.AggregationBuilder;
import io.redisearch.aggregation.reducers.Reducer;
import io.redisearch.aggregation.reducers.Reducers;

import java.util.HashMap;
import java.util.Map;

public class SearchEngineFilter {
    private int minBaths;
    private int minBeds;
    private double minTaxValue;
    private double maxTaxValue;
    private int minYearBuilt;
    private double minLotSize;
    private double maxLotSize;
    private String zipCode;
    private String geo;
    private double distance;
    private String unit;
    private String groupBy;
    private String reduce;
    private String reducer;
    private int limit;
    private boolean ascending;

    private static final Map<String, String> columns = new HashMap<String, String>()
    {{
        put("Bathrooms", "bathroomcnt");
        put("Bedrooms", "bedroomcnt");
        put("Tax Value", "taxvaluedollarcnt");
        put("Year Built", "yearbuilt");
        put("Lot Size", "lotsizesquarefeet");
        put("Zip Code", "regionidzip");
    }};

    private static final String template = "@bathroomcnt:[%d, +inf] @bedroomcnt:[%d inf]"
        + " @taxvaluedollarcnt:[%.2f %.2f] @yearbuilt:[%d +inf]"
        + " @lotsizesquarefeet:[%.2f %.2f]";

    public int getMinBaths() {
        return minBaths;
    }

    public void setMinBaths(int minBaths) {
        this.minBaths = minBaths;
    }

    public int getMinBeds() {
        return minBeds;
    }

    public void setMinBeds(int minBeds) {
        this.minBeds = minBeds;
    }

    public double getMinTaxValue() {
        return minTaxValue;
    }

    public void setMinTaxValue(double minTaxValue) {
        this.minTaxValue = minTaxValue;
    }

    public double getMaxTaxValue() {
        return maxTaxValue;
    }

    public void setMaxTaxValue(double maxTaxValue) {
        this.maxTaxValue = maxTaxValue;
    }

    public int getMinYearBuilt() {
        return minYearBuilt;
    }

    public void setMinYearBuilt(int minYearBuilt) {
        this.minYearBuilt = minYearBuilt;
    }

    public double getMinLotSize() {
        return minLotSize;
    }

    public void setMinLotSize(double minLotSize) {
        this.minLotSize = minLotSize;
    }

    public double getMaxLotSize() {
        return maxLotSize;
    }

    public void setMaxLotSize(double maxLotSize) {
        this.maxLotSize = maxLotSize;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setGeo(String geo) { this.geo = geo; }

    public String getGeo() { return geo; }

    public double getDistance() { return distance; }

    public void setDistance(double distance) { this.distance = distance; }

    public String getUnit() { return unit; }

    public void setUnit(String unit) { this.unit = unit; }

    public String getGroupBy() { return groupBy; }

    public void setGroupBy(String groupBy) { this.groupBy = groupBy; }

    public String getReduce() { return reduce; }

    public void setReduce(String reduce) { this.reduce = reduce; }

    public String getReducer() { return reducer; }

    public void setReducer(String reducer) { this.reducer = reducer; }

    public String getDBPropertyName(String columnName) { return columns.get(columnName); }

    public void setLimit(Integer limit) { this.limit = limit; }

    public int getLimit() {return limit; }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public String getQuery() {
        String query = String.format(template, minBaths, minBeds,
                minTaxValue, maxTaxValue,
                minYearBuilt, minLotSize, maxLotSize);
        if ( !zipCode.isEmpty() )
            query += " @regionidzip:" + zipCode;
        if ( !geo.isEmpty() && distance > 0 ) {
            query += " @geopos:[" + geo + " " + distance + " " + unit + "]";
        }
        return query;
    }

    public AggregationBuilder getAggregation() {
        AggregationBuilder builder = new AggregationBuilder(getQuery());

        String groupByColumn = getDBPropertyName(groupBy);
        String reduceColumn = getDBPropertyName(reduce);
        if ( reducer.equals("Avg")) {
            builder.groupBy("@" + groupByColumn, Reducers.avg("@" + reduceColumn).as(reduceColumn));
        }
        else
        if ( reducer.equals("Sum")) {
            builder.groupBy("@" + groupByColumn, Reducers.sum("@" + reduceColumn).as(reduceColumn));
        }
        else
        if ( reducer.equals("Min")) {
            builder.groupBy("@" + groupByColumn, Reducers.min("@" + reduceColumn).as(reduceColumn));
        }
        else
        if ( reducer.equals("Max")) {
            builder.groupBy("@" + groupByColumn, Reducers.max("@" + reduceColumn).as(reduceColumn));
        }
        else
        if ( reducer.equals("Count")) {
            builder.groupBy("@" + groupByColumn, Reducers.count().as(reduceColumn));
        }

        if ( ascending )
            builder.sortByAsc("@" + reduceColumn);
        else
            builder.sortByDesc("@" + reduceColumn);

        builder.limit(0, limit);
        return builder;
    }

}
