package com.spring.mstransferbank.entity;

import lombok.Data;


@Data
public class Customer {

    String id;

    String name;

    String lastName;

    TypeCustomer typeCustomer;

    String dni;

    Integer age;

    String gender;
}
