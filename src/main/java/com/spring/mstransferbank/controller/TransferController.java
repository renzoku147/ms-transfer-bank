package com.spring.mstransferbank.controller;

import com.spring.mstransferbank.entity.*;
import com.spring.mstransferbank.service.TransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RefreshScope
@RestController
@RequestMapping("/transfer")
@Slf4j
public class TransferController {

    @Autowired
    TransferService transferService;

    @GetMapping("list")
    public Flux<Transfer> findAll(){
        return transferService.findAll();
    }

    @GetMapping("/find/{id}")
    public Mono<Transfer> findById(@PathVariable String id){
        return transferService.findById(id);
    }

    @GetMapping("/findAccount/{accountNumber}")
    public Mono<BankAccount> findAccountBank(@PathVariable String accountNumber){
        System.out.println("Controller INICIO");
        return transferService.findBankAccount(accountNumber)
                .filter(accountBank -> {
                    System.out.println("Controller filter > " + accountBank.isPresent() );
                    return accountBank.isPresent();
                })
                .map(optionalAccountBank -> {
                    BankAccount ba = optionalAccountBank.get();
                    if(ba instanceof CurrentAccount){
                        System.out.println("Controller casteo1 > " + (CurrentAccount) ba );
                        return (CurrentAccount) ba;
                    }
                    if(ba instanceof FixedTerm){
                        System.out.println("Controller casteo2 > " + (FixedTerm) ba );
                        return (FixedTerm) ba;
                    }
                    if(ba instanceof SavingAccount){
                        System.out.println("Controller casteo3 > " + (SavingAccount) ba );
                        return (SavingAccount) ba;
                    }
                    return null;
                });
    }

    public Mono<BankAccount> findAccountBankInfo(String accountNumber){
        System.out.println("Controller INICIO");
        return transferService.findBankAccount(accountNumber)
                .filter(accountBank -> {
                    System.out.println("Controller filter > " + accountBank.isPresent() );
                    return accountBank.isPresent();
                })
                .map(optionalAccountBank -> {
                    BankAccount ba = optionalAccountBank.get();
                    if(ba instanceof CurrentAccount){
                        System.out.println("Controller casteo1 > " + (CurrentAccount) ba );
                        return (CurrentAccount) ba;
                    }
                    if(ba instanceof FixedTerm){
                        System.out.println("Controller casteo2 > " + (FixedTerm) ba );
                        return (FixedTerm) ba;
                    }
                    if(ba instanceof SavingAccount){
                        System.out.println("Controller casteo3 > " + (SavingAccount) ba );
                        return (SavingAccount) ba;
                    }
                    return null;
                });
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Transfer>> create(@Valid @RequestBody Transfer transfer){
        return findAccountBankInfo(transfer.getOriginAccount())
            .flatMap(origin -> {
                        if(origin instanceof CurrentAccount){
                            ((CurrentAccount)origin).setBalance(((CurrentAccount)origin).getBalance() - transfer.getAmountTransference());
                        }
                        if(origin instanceof FixedTerm){
                            ((FixedTerm)origin).setBalance(((FixedTerm)origin).getBalance() - transfer.getAmountTransference());
                        }
                        if(origin instanceof SavingAccount){
                            ((SavingAccount)origin).setBalance(((SavingAccount)origin).getBalance() - transfer.getAmountTransference());
                        }
                        return transferService.updateBankAccountBalance(origin)
                                .flatMap(originUpdate -> findAccountBankInfo(transfer.getDestinationAccount())
                                        .flatMap(destiny -> {
                                                    if(destiny instanceof CurrentAccount){
                                                        ((CurrentAccount)destiny).setBalance(((CurrentAccount)destiny).getBalance() + transfer.getAmountTransference());
                                                    }
                                                    if(destiny instanceof FixedTerm){
                                                        ((FixedTerm)destiny).setBalance(((FixedTerm)destiny).getBalance() + transfer.getAmountTransference());
                                                    }
                                                    if(destiny instanceof SavingAccount){
                                                        ((SavingAccount)destiny).setBalance(((SavingAccount)destiny).getBalance() + transfer.getAmountTransference());
                                                    }
                                                    return transferService.updateBankAccountBalance(destiny)
                                                            .flatMap(destinyUpdate -> transferService.create(transfer));

                                                }
                                        ));
                    }
            )
            .map(ft -> new ResponseEntity<>(ft, HttpStatus.CREATED))
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

    }

    @PutMapping("/update")
    public Mono<ResponseEntity<Transfer>> update(@RequestBody Transfer transfer) {
        return null;
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<String>> delete(@PathVariable String id) {
        return null;
    }
}
