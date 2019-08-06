package com.ka.revolution.model.com.response;

import com.ka.revolution.model.persistence.Account;
import lombok.Data;

import java.util.List;

@Data
public class GetAccountsResponse {

    private List<Account> accounts;

}
