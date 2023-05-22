package com.example.customerservice.Models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String fullName;
    @Column (unique = true)
    private String ssn;
    @Embedded
    private Address address;
    private String email;

    @ElementCollection
    private List<Item> wishlist = new ArrayList<>();


    public Customer() {
    }

    public Customer(String fullName, String ssn, Address address, String email) {
        this.fullName = fullName;
        this.ssn = ssn;
        this.address = address;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Item> getWishlist() {
        return wishlist;
    }

    public void setWishlist(List<Item> wishlist) {
        this.wishlist = wishlist;
    }

    public void addToWishlist (Item item){
        wishlist.add(item);
    }
}