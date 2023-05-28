package com.example.customerservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Orders {
    @JsonIgnore
    private Long id;
    private LocalDate date;
    @NotNull(message = "Items can not be given for free")
    private int sum;
    private Long customerId; //Ska kunna raderas och bli null
    @NotEmpty
    private List<Long> itemIds;
}
