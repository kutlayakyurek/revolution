package com.ka.revolution;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class TestConstants {

    public String TEST_FILE = "transfer-request.json";
    public String FIRST_ACCOUNT_NAME = "Iron Man";
    public BigDecimal FIRST_ACCOUNT_AMOUNT = BigDecimal.valueOf(100000);
    public Long FIRST_ACCOUNT_ID = 1l;
    public String SECOND_ACCOUNT_NAME = "Captain America";
    public BigDecimal SECOND_ACCOUNT_AMOUNT = BigDecimal.valueOf(150000);
    public Long SECOND_ACCOUNT_ID = 2l;
    public String THIRD_ACCOUNT_NAME = "Ant Man";
    public BigDecimal THIRD_ACCOUNT_AMOUNT = BigDecimal.valueOf(200000);

    public int TEST_PORT = 8080;
    public String CONTEXT_ADDRESS = "http://localhost:8080";
    public String RESOURCE_ACCOUNT = "/account";
    public String PATH_TRANSFER = "/transfer";

}
