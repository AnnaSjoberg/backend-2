package com.example.customerservice.Repos;


import com.example.customerservice.Models.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findBySsn(String ssn);
}