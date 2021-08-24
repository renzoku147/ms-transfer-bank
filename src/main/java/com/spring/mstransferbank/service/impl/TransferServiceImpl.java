package com.spring.mstransferbank.service.impl;

import com.spring.mstransferbank.entity.*;
import com.spring.mstransferbank.repository.TransferRepository;
import com.spring.mstransferbank.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService {

    WebClient webClientCurrent = WebClient.create("http://localhost:8887/ms-current-account/currentAccount");

    WebClient webClientFixed = WebClient.create("http://localhost:8887/ms-fixed-term/fixedTerm");

    WebClient webClientSaving = WebClient.create("http://localhost:8887/ms-saving-account/savingAccount");

    @Autowired
    TransferRepository transferRepository;

    @Override
    public Mono<Transfer> create(Transfer t) {
        return transferRepository.save(t);
    }

    @Override
    public Flux<Transfer> findAll() {
        return transferRepository.findAll();
    }

    @Override
    public Mono<Transfer> findById(String id) {
        return transferRepository.findById(id);
    }

    @Override
    public Mono<Transfer> update(Transfer t) {
        return null;
    }

    @Override
    public Mono<Boolean> delete(String t) {
        return null;
    }

    @Override
    public Mono<Optional<BankAccount>> findBankAccount(String cardNumber) {
        return webClientCurrent.get().uri("/findByAccountNumber/{id}", cardNumber)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(CurrentAccount.class)
                .map(currentAccount -> {
                    System.out.println("Encontro currentAccount > " + currentAccount.getId());
                    return Optional.of((BankAccount)currentAccount);})
                .switchIfEmpty(webClientFixed.get().uri("/findByAccountNumber/{id}", cardNumber)
                                .accept(MediaType.APPLICATION_JSON)
                                .retrieve()
                                .bodyToMono(FixedTerm.class)
                                .map(fixedTerm -> {
                                    System.out.println("Encontro fixedTerm > " + fixedTerm.getId());
                                    return Optional.of((BankAccount)fixedTerm);
                                })
                                .switchIfEmpty(webClientSaving.get().uri("/findByAccountNumber/{id}", cardNumber)
                                                .accept(MediaType.APPLICATION_JSON)
                                                .retrieve()
                                                .bodyToMono(SavingAccount.class)
                                                .map(savingAccount -> {
                                                    System.out.println("Encontro savingAccount > " + savingAccount.getId());
                                                    return Optional.of((BankAccount)savingAccount);
                                                }))
                                                .defaultIfEmpty(Optional.empty())
                                );
    }

    @Override
    public Mono<BankAccount> updateBankAccountBalance(BankAccount account) {
        if(account instanceof CurrentAccount){
            return webClientCurrent.put().uri("/updateTransference")
                    .accept(MediaType.APPLICATION_JSON)
                    .syncBody((CurrentAccount)account)
                    .retrieve()
                    .bodyToMono(CurrentAccount.class)
                    .map(ca -> (BankAccount)ca);
        }
        if(account instanceof FixedTerm){
            return webClientFixed.put().uri("/updateTransference")
                    .accept(MediaType.APPLICATION_JSON)
                    .syncBody((FixedTerm)account)
                    .retrieve()
                    .bodyToMono(FixedTerm.class)
                    .map(ft -> (BankAccount)ft);
        }
        if(account instanceof SavingAccount){
            return webClientSaving.put().uri("/updateTransference")
                    .accept(MediaType.APPLICATION_JSON)
                    .syncBody((SavingAccount)account)
                    .retrieve()
                    .bodyToMono(SavingAccount.class)
                    .map(ft -> (BankAccount)ft);
        }
        return Mono.empty();
    }


}
