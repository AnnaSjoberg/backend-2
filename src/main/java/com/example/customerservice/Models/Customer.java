package com.example.customerservice.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private Set<Long> wishlist = new HashSet<>();


    public Customer(String fullName, String ssn, Address address, String email) {
        this.fullName = fullName;
        this.ssn = ssn;
        this.address = address;
        this.email = email;
    }

    public void addToWishlist (Long itemId){
        wishlist.add(itemId);
    }

    public void removeFromWishlist (Long itemId){
        wishlist.remove(itemId);
    }
}