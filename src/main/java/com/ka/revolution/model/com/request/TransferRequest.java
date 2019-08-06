package com.ka.revolution.model.com.request;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class TransferRequest {

    @NonNull
    private Long destinationAccountId;

    @NonNull
    private BigDecimal amount;

}
