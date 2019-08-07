package com.ka.revolution.model.com.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaveAccountRequest {

    private BigDecimal amount;
    private String fullName;

}
