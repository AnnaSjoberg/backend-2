package com.example.customerservice.Controllers;

import com.example.customerservice.Models.Address;
import com.example.customerservice.Models.Customer;
import com.example.customerservice.Models.Item;
import com.example.customerservice.Models.ItemDTO;
import com.example.customerservice.Repos.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/customers")
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${item-service.url}")
    private String itemServiceBaseUrl;
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CustomerRepository customerRepo;

    @PostMapping(path = "/add") // Map ONLY POST Requests
    public @ResponseBody String addNewCustomer(@RequestParam String name,
                                               @RequestParam String email,
                                               @RequestParam String ssn,
                                               @RequestParam String street,
                                               @RequestParam String zipcode,
                                               @RequestParam String postalAddress,
                                               @RequestParam String country) {

        customerRepo.save(new Customer(name, ssn, new Address(street, zipcode, postalAddress, country), email));
        return "Customer saved";
    }

    @GetMapping(path = "/getAll")
    public List<Customer> getAllCustomers() {
        // This returns a JSON or XML with the users
        return customerRepo.findAll();
    }

        @GetMapping(path="/getAll2")
        public @ResponseBody List<Customer> getAllCustomers2() {
            // This returns a JSON or XML with the users
            return customerRepo.findAll();
        }


    @GetMapping(path = "/getById/{id}")
    public @ResponseBody Customer getById(@PathVariable Long id) {
        return customerRepo.findById(id).orElse(null);
    }

    @GetMapping(path = "/getBySsn/{ssn}")
    public @ResponseBody Customer getBySsn(@PathVariable String ssn) {
        return customerRepo.findBySsn(ssn);
    }

    @GetMapping(path = "/deleteById/{id}")
    public @ResponseBody String deleteById(@PathVariable Long id) {
        customerRepo.deleteById(id);
        return "Customer deleted";
    }
    
    @GetMapping(path = "/{customerId}/wishlist")
    public @ResponseBody List<ItemDTO> getWishlist(@PathVariable Long customerId) {
        String itemResourceUrl = itemServiceBaseUrl + "items/getById/{id}";
        Customer c = customerRepo.findById(customerId).orElse(null);
        Set<Item> itemIds = c.getWishlist();
        List<ItemDTO> wishlist = new ArrayList<>();
        for (Item item : itemIds) {
            ItemDTO dto = restTemplate.getForObject(itemResourceUrl, ItemDTO.class, item.getItemID());
            if (dto != null) {
                wishlist.add(dto);
            }
        }
        return wishlist;
    }

    //curl -X POST -H "Content-Type: application/json" "http://localhost:8080/orders/buy?customerId=1&itemIds=2&itemIds=3"
    @PostMapping(path = "/{customerId}/wishlist/add")
    public List<String> addToWishlist(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {
        List<String> result = new ArrayList<>();
        String itemResourceUrl = itemServiceBaseUrl + "items/getById/{id}";
        Customer c = customerRepo.findById(customerId).orElse(null);
        for (Long itemId : itemIds) {
            if (!c.getWishlist().contains(new Item(itemId))) {
                ItemDTO dto = restTemplate.getForObject(itemResourceUrl, ItemDTO.class, itemId);
                if (dto != null) {
                    c.addToWishlist(new Item(itemId));
                    result.add("Item "+itemId+" has been added to wishlist");
                }else {
                    result.add("No matching item found");
                }
            }
        }
        customerRepo.save(c);
        return result;
    }
    @PostMapping(path = "/{customerId}/wishlist/remove")
    public List<String> removeFromWishlist(@RequestParam Long customerId, @RequestParam List<Long> itemIds) {
        List<String> result = new ArrayList<>();
        Customer c = customerRepo.findById(customerId).orElse(null);
        for (Long itemId : itemIds) {
            c.removeFromWishlist(new Item(itemId));
            result.add("Item has been removed from wishlist");
        }
        customerRepo.save(c);
        return result;
    }

}