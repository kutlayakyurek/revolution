package com.ka.revolution.main;

import com.ka.revolution.controller.AccountController;
import com.ka.revolution.repository.InMemoryAccountRepository;
import com.ka.revolution.service.AccountServiceImpl;
import express.Express;

public class Application {

    public static void main(String[] args) {
        final Express server = new Express();
        server.bind(new AccountController(new AccountServiceImpl(new InMemoryAccountRepository())));
        server.listen(8080);
    }

}
