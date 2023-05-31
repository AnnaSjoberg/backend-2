package com.example.customerservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
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
    @NotNull
    @Past(message = "Enter valid date.")
    private LocalDate date;
    @Min(value = 1)
    private int sum;
    private Long customerId;
    @NotEmpty
    private List<Long> itemIds;
}
