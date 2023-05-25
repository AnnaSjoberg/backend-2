package com.example.customerservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Item {
    private String name;
    private String price;
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private int stock;

}
