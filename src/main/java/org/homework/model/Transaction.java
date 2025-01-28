package org.homework.model;

import lombok.Data;

@Data
public class Transaction {
    private String category;
    private long amount;

    public Transaction(String category, long amount) {
        this.category = category;
        this.amount = amount;
    }
}
