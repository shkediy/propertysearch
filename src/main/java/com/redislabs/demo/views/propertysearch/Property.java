package com.redislabs.demo.views.propertysearch;

public class Property {

    private String  id;
    private double bathroomCount;
    private int bedroomCount;
    private double taxValue;
    private int yearBuilt;
    private int lotSize;
    private String zipcode;
    private String geo;

    public Property(String id,
                    double bathroomCount,
                    int bedroomCount,
                    double taxValue,
                    int yearBuilt,
                    int lotSize,
                    String zipcode,
                    String geo) {
        this.id = id;
        this.bathroomCount = bathroomCount;
        this.bedroomCount = bedroomCount;
        this.taxValue = taxValue;
        this.yearBuilt = yearBuilt;
        this.lotSize = lotSize;
        this.zipcode = zipcode;
        this.geo = geo;
    }

    public String  getId() {
        return id;
    }

    public double getBathroomCount() {
        return bathroomCount;
    }

    public int getBedroomCount() {
        return bedroomCount;
    }

    public double getTaxValue() {
        return taxValue;
    }

    public int getYearBuilt() {
        return yearBuilt;
    }

    public int getLotSize() {
        return lotSize;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getGeo() { return geo; }
}