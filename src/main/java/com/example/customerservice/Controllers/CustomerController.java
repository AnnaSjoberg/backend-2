package com.example.customerservice.Controllers;

import com.example.customerservice.Models.*;
import com.example.customerservice.Repos.CustomerRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@Validated
@RequestMapping(path = "/customers")
@Tag(name = "Customers", description = "Operations related to managing data concerning Customers")
public class CustomerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${item-service.url}")
    private String itemServiceBaseUrl;
    @Value("${orders-service.url}")
    private String ordersServiceBaseUrl;
    @Autowired
    private CustomerRepository customerRepo;


    @GetMapping("/welcome")
    @Operation(summary = "Welcome page", description = "After login user is redirected to this page. Returns a String.")
    public String welcome() {
        return "Login successful. Welcome to the Customer-Service";
    }


    @GetMapping(path = "/getAll")
    @Operation(summary = "Fetches all Customers", description = "Fetches all Customers in db and returns them as a List in JSON format")
    public @ResponseBody List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }


    @GetMapping("/getById/{id}")
    @Operation(summary = "fetches a Customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetch of Customer successful",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The request was malformed or had invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content)
    })
    public @ResponseBody Customer getById(@PathVariable Long id) {
        return customerRepo.findById(id).orElse(null);
    }


    @GetMapping(path = "/getBySsn/{ssn}")
    @Operation(summary = "fetches a Customer by its Social security number. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetch of Customer successful",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The request was malformed or had invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated request",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content)
    })
    public @ResponseBody Customer getBySsn(@Valid @PathVariable String ssn) {
        return customerRepo.findBySsn(ssn);
    }

    @PostMapping(path = "/add")
    @Operation(summary = "Adds a new Customer", description = "Stores a new Customer in the database. Requires Admin privileges")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String addNewCustomer(@Valid @RequestBody Customer customer) {
        customerRepo.save(customer);
        return "customer added successfully";
    }

    @GetMapping(path = "/deleteById/{id}")
    @Operation(summary = "Deletes a Customer by its ID", description = "Deletes a Customer from db. Requires Admin privileges")
    @PreAuthorize("hasRole('ADMIN')")
    public @ResponseBody String deleteById(@PathVariable Long id) {
        customerRepo.deleteById(id);
        return "Customer deleted";
    }

    @GetMapping("/{customerId}/getOrders")
    @Operation(summary = "fetches all orders placed by a Customer. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetch of orders successful",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The request was malformed or had invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated request",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content)
    })
    public ResponseEntity<Orders[]> getOrdersByCustomerId(@PathVariable Long customerId) {
        String ordersResourceUrl = ordersServiceBaseUrl + "/orders/getByCustomerId/{customerId}";
        Customer c = customerRepo.findById(customerId).orElse(null);

        if (c == null) {
            return ResponseEntity.notFound().build();
        }

        Orders[] ordersArray = restTemplate.getForObject(ordersResourceUrl, Orders[].class, customerId);

        return (ordersArray != null && ordersArray.length > 0) ? ResponseEntity.ok(ordersArray) : ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{customerId}/wishlist")
    @Operation(summary = "fetches a Customer's wishlist. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Fetch of wishlist successful",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The request was malformed or had invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated request",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content)
    })
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
    @Operation(summary = "Add items to a Customer's wishlist. Requires authentication.")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Item/items added successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The request was malformed or had invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated request",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content)
    })
    public @ResponseBody List<String> addToWishlist(@PathVariable Long customerId, @Valid @RequestBody List<Long> itemIds) {
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
    @Operation(summary = "Remove items from a Customer's wishlist. Requires authentication")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Item/items added successfully",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400",
                    description = "The request was malformed or had invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401",
                    description = "Unauthenticated request",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content)
    })
    public List<String> removeFromWishlist(@PathVariable Long customerId, @Valid @RequestBody List<Long> itemIds) {
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
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