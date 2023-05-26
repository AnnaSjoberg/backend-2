package com.example.customerservice.Controllers;

import com.example.customerservice.Models.*;
import com.example.customerservice.Repos.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/customers")
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${item-service.url}")
    private String itemServiceBaseUrl;
    @Value("${orders-service.url}")
    private String ordersServiceBaseUrl;
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private CustomerRepository customerRepo;

    @PostMapping(path = "/add") // Map ONLY POST Requests
    public @ResponseBody String addNewCustomer(@RequestBody Customer customer) {

        customerRepo.save(customer);
        return "customer added successfully";
    }

    /*
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
     */

    @GetMapping()
    public String welcome() {

        return "Welcome to Customers!";
    }
    @GetMapping("/adder")
    public String adder() {

        return "Only Admin!";
    }
    @GetMapping(path = "/getAll")
    public @ResponseBody List<Customer> getAllCustomers() {
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
    @GetMapping("/{customerId}/getOrdersRE")
    public ResponseEntity<Orders[]> getOrdersByCustomerId(@PathVariable Long customerId) {
        String ordersResourceUrl = ordersServiceBaseUrl + "/orders/getByCustomerId/{customerId}";
        Customer c = customerRepo.findById(customerId).orElse(null);

        if (c == null) {
            return ResponseEntity.notFound().build();
        }

        Orders[] ordersArray = restTemplate.getForObject(ordersResourceUrl, Orders[].class, customerId);

        return (ordersArray != null && ordersArray.length > 0) ? ResponseEntity.ok(ordersArray) : ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}/getOrdersLO")
    public @ResponseBody List<Orders> getOrdersByCustomerId2(@PathVariable Long customerId) {
        String ordersResourceUrl = ordersServiceBaseUrl + "/orders/getByCustomerId/{customerId}";
        Customer c = customerRepo.findById(customerId).orElse(null);
        List<Orders> ordersList = new ArrayList<>();
        if (c != null) {
             ordersList = Arrays.asList(restTemplate.getForObject(ordersResourceUrl, Orders[].class, customerId));
        }
        return ordersList;
    }

    @GetMapping(path = "/{customerId}/wishlist")
    public @ResponseBody List<Item> getWishlist(@PathVariable Long customerId) {
        String itemResourceUrl = itemServiceBaseUrl + "/items/getById/{id}";
        Customer c = customerRepo.findById(customerId).orElse(null);
        List<Item> wishlistAsDTO = new ArrayList<>();
        if (c != null) {
            Set<Long> itemIds = c.getWishlist();
            for (Long itemId : itemIds) {
                Item dto = restTemplate.getForObject(itemResourceUrl, Item.class, itemId);
                if (dto != null) {
                    wishlistAsDTO.add(dto);
                }
            }
        }
        return wishlistAsDTO;
    }

    @PostMapping(path = "/{customerId}/wishlist/add")
    public @ResponseBody List<String> addToWishlist(@PathVariable Long customerId, @RequestBody List<Long> itemIds) {
        List<String> result = new ArrayList<>();
        String itemResourceUrl = itemServiceBaseUrl + "/items/getById/{id}";
        Customer c = customerRepo.findById(customerId).orElse(null);
        for (Long itemId : itemIds) {
            if (!c.getWishlist().contains(itemId)) {
                Item dto = restTemplate.getForObject(itemResourceUrl, Item.class, itemId);
                if (dto != null) {
                    c.addToWishlist(itemId);
                    result.add("Item " + itemId + " has been added to wishlist");
                } else {
                    result.add("No matching item found for item " + itemId);
                }
            }
        }
        customerRepo.save(c);
        return result;
    }


    @PostMapping(path = "/{customerId}/wishlist/remove")
    public List<String> removeFromWishlist(@PathVariable Long customerId, @RequestBody List<Long> itemIds) {
        List<String> result = new ArrayList<>();
        Customer c = customerRepo.findById(customerId).orElse(null);

        if (c.getWishlist().size() == 0) {
            result.add("Empty list, nothing to remove");

        } else {
            for (Long itemId : itemIds) {
                c.removeFromWishlist(itemId);
                result.add("Item " + itemId + " has been removed from wishlist");
            }
        }
        customerRepo.save(c);
        return result;
    }


    //dessa kanske man egentligen inte är intresserad av att använda i verkligheten?
    @GetMapping("/getAllItems")
    public @ResponseBody Item[] getItems() {
        String itemResourceUrl = itemServiceBaseUrl + "/items/getAll";
        return restTemplate.getForObject(itemResourceUrl, Item[].class);
    }

    @GetMapping("/getAllOrders")
    public @ResponseBody Orders[] getOrders() {
        String ordersResourceUrl = ordersServiceBaseUrl + "/orders/getAll";
        return restTemplate.getForObject(ordersResourceUrl, Orders[].class);
    }
}