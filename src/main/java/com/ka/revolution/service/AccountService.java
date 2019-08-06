package com.ka.revolution.service;

import com.ka.revolution.model.com.SaveAccountRequest;
import com.ka.revolution.model.persistence.Account;

public interface AccountService {

    Account saveAccount(SaveAccountRequest request);

}
