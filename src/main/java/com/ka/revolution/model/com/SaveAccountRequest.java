package com.ka.revolution.model.com;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

@Data
public class SaveAccountRequest {

    private BigDecimal amount = BigDecimal.ZERO;

    @NonNull
    private String fullName;

}
