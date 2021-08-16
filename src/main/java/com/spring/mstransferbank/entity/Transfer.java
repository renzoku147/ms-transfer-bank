package com.spring.mstransferbank.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@Document("Transfer")
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    String id;

    @NotBlank
    @NotNull
    String originAccount;

    @NotBlank
    @NotNull
    String destinationAccount;

    @NotNull
    Double amountTransference;

    @NotNull
    String codeTransference;

    String descriptionTransference;

    LocalDateTime dateTransference;

}
