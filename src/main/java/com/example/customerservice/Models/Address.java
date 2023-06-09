package com.example.customerservice.Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {
    @NotBlank(message = "Street must be set")
    private String street;
    @NotBlank(message = "Zip code must be set")
    @Pattern(regexp = "\\d{5}", message = "Zip code must consist of exactly 5 digits and nothing else")
    private String zipCode;
    @NotBlank(message = "Postal address must be set")
    private String postalAddress;
    @NotBlank(message = "Country must be set")
    private String Country;
}
