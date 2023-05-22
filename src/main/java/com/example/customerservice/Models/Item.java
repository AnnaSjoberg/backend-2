package com.example.customerservice.Models;

import jakarta.persistence.Embeddable;

@Embeddable
public class Item {
    private Long itemID;

    public Long getItemID() {
        return itemID;
    }

    public void setItemID(Long itemID) {
        this.itemID = itemID;
    }
}
