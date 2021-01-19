package com.redislabs.demo.views.addresssearch;

public class Address {

    private String number;
    private String street;
    private String unit;
    private String city;
    private String postalCode;
    private String state;
    private String geo;


    public Address(String  number,
            String street,
            String unit,
            String city,
            String postalCode,
            String state,
            String geo) {
        this.number = number;
        this.street = street;
        this.unit = unit;
        this.city = city;
        this.postalCode = postalCode;
        this.state = state;
        this.geo = geo;


    }

    public String  getNumber() {
        return number;
    }

    public String getStreet() {
        return street;
    }

    public String getUnit() {
        return unit;
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

    public String getGeo() { return geo; }

}