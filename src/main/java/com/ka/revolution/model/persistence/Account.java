package com.ka.revolution.model.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class Account {

    private Long id;
    private BigDecimal amount;
    private String fullName;

}
