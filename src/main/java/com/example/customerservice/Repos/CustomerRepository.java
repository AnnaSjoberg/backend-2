package com.example.customerservice.Repos;


import com.example.customerservice.Models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findBySsn(String ssn);
}