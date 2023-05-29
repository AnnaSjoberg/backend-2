package com.example.customerservice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @NotBlank(message = "Full name missing")
    private String fullName;
    @Column (unique = true)
    @NotBlank(message = "Must not be blank")
    @Size(min = 9, max = 13, message = "Ssn must be unique and between 9 and 13 characters")
    private String ssn;
    @Embedded
    @NotBlank(message = "Must not be blank")
    private Address address;
    @Email(message = "E-mail must be set properly")
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