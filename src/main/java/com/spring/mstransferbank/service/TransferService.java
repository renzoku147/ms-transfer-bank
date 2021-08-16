package com.spring.mstransferbank.service;

import com.spring.mstransferbank.entity.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface TransferService {
    Mono<Transfer> create(Transfer t);

    Flux<Transfer> findAll();

    Mono<Transfer> findById(String id);

    Mono<Transfer> update(Transfer t);

    Mono<Boolean> delete(String t);

    Mono<Optional<BankAccount>> findBankAccount(String cardNumber);

    Mono<BankAccount> updateBankAccountBalance(BankAccount numberAccount);

}
