package com.example.customerservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {

    @Id
    @JsonIgnore
    private Long id;
    @NotBlank(message = "Must not be blank")
    @Size(min = 1 , max = 100, message = "Item name must be between 1 and 100 characters long")
    private String name;
    @Min(value = 1, message = "Item is not supposed to be given free!")
    private int price;
    @JsonIgnore
    @Min(value = 0, message = "No value below 0!")
    private int stock;

}
