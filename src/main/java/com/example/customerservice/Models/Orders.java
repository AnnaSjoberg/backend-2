package com.example.customerservice.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private int sum;
    private Long customerId;
    private List<Long> itemIds;
}
