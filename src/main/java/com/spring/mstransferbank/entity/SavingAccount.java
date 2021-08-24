package com.spring.mstransferbank.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SavingAccount implements BankAccount {
    String id;

    private Customer customer;

    private String accountNumber;

    private List<Person> holders;

    private List<Person> signers;

    private Integer limitTransactions;

    private Integer freeTransactions;

    private Double commissionTransactions;

    private Double balance;

    private Double minAverageVip;

    private LocalDateTime date;



}
