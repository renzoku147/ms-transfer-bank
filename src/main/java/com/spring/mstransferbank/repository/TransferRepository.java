package com.spring.mstransferbank.repository;

import com.spring.mstransferbank.entity.Transfer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransferRepository extends ReactiveMongoRepository<Transfer, String> {
}
