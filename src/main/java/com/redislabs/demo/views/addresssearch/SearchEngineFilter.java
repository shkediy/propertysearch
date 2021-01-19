package com.redislabs.demo.views.addresssearch;

import java.util.HashMap;
import java.util.Map;

public class SearchEngineFilter {


    private String free;
    private String street;
    private String city;
    private String postalCode;
    private String state;
    private String geo;
    private double distance;
    private String distUnit;
    private boolean phonetic;
    private boolean inOrder;
    private boolean slop;
    private int slopValue;
    private String sort;
    private boolean ascending;
    private boolean highlight;

    private static final Map<String, String> columns = new HashMap<String, String>()
    {{
        put("Number", "NUMBER");
        put("Street", "STREET");
        put("Unit", "UNIT");
        put("City", "CITY");
        put("Postal Code", "POSTCODE");
        put("State", "STATE");
    }};

    public String getFree() {
        return free;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getState() {
        return state;
    }

    public double getDistance() {
        return distance;
    }

    public String getDistUnit() {
        return distUnit;
    }

    public String getQuery() {
        String query = "";
        if ( !street.isEmpty() )
            query += " @STREET:" + street;
        if ( !city.isEmpty() )
            query += " @CITY:" + city;
        if ( !postalCode.isEmpty() )
            query += " @POSTCODE:" + postalCode;
        if ( !state.isEmpty() )
            query += " @STATE:" + state;

        if ( !geo.isEmpty() && distance > 0 ) {
            query += " @GEOPOS:[" + geo + " " + distance + " " + distUnit + "]";
        }

        if ( !free.isEmpty() ) {
            String freeQuery = "(" + free + ")";
            if ( !phonetic || inOrder || slop ) {
                String queryExt = "=>{ ";
                if (!phonetic)
                    queryExt += "$phonetic: false; ";
                if (inOrder)
                    queryExt += "$inorder: true; ";
                if ( slop )
                    queryExt += "$slop: " + slopValue + ";";
                queryExt += "}";
                freeQuery += queryExt;
            }
            query += " " + freeQuery ;
        }
        return query;
    }


    public void setFree(String text) {
        this.free = text;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getGeo() {
        return geo;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setDistUnit(String distUnit) {
        this.distUnit = distUnit;
    }

    public void setPhonetic(Boolean phonetic) {
        this.phonetic = phonetic;
    }

    public Boolean isPhonetic() {
        return phonetic;
    }

    public void setInOrder(Boolean inOrder) {
        this.inOrder = inOrder;
    }

    public Boolean isInOrder() {
        return inOrder;
    }

    public boolean isSlop() {
        return slop;
    }

    public void setSlop(boolean slop) {
        this.slop = slop;
    }

    public int getSlopValue() {
        return slopValue;
    }

    public void setSlopValue(int value) {
        this.slopValue = value;
    }

    public void setSort(String sort) {
        this.sort = columns.get(sort);
    }

    public String getSort() {
        return sort;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setHighlight(Boolean highlight) {
        this.highlight = highlight;
    }

    public Boolean getHighlight() {
        return highlight;
    }
}
