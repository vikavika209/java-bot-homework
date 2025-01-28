package org.homework.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Expenses {
    private double plan;
    private List<Transaction> transactions;

    public Expenses() {
        transactions = new ArrayList<Transaction>();
    }
}