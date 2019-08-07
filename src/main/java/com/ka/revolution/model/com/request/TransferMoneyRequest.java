package com.ka.revolution.model.com.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransferMoneyRequest {

    private Long destinationAccountId;
    private BigDecimal amount;

}
