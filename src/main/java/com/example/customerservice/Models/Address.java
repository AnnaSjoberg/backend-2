package com.example.customerservice.Models;

import jakarta.persistence.*;

@Embeddable
public class Address {
    private String street;
    private String zipCode;
    private String postalAddress;
    private String Country;


    public Address() {
    }

    public Address(String street, String zipCode, String postalAddress, String country) {
        this.street = street;
        this.zipCode = zipCode;
        this.postalAddress = postalAddress;
        Country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }
}
