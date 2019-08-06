package com.ka.revolution.controller;

import com.ka.revolution.model.com.request.SaveAccountRequest;
import com.ka.revolution.model.com.response.GetAccountsResponse;
import com.ka.revolution.service.AccountService;
import com.ka.revolution.util.FileUtil;
import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class AccountController extends AbstractController {

    private AccountService accountService;

    @DynExpress(context = "/account", method = RequestMethod.GET)
    public void getAccounts(final Request request, final Response response) throws IOException {
        final GetAccountsResponse getAccountsResponse = new GetAccountsResponse();
        getAccountsResponse.setAccounts(accountService.getAccounts());
        
        sendResponse(response, FileUtil.convertObjectToJson(getAccountsResponse));
    }

    @DynExpress(context = "/account", method = RequestMethod.POST)
    public void saveAccount(final Request request, final Response response) throws IOException {
        final SaveAccountRequest saveAccountRequest = FileUtil
                .convertJsonStreamToObject(request.getBody(), SaveAccountRequest.class);
        log.debug(saveAccountRequest.toString());

        if (saveAccountRequest == null) {
            sendErrorResponse(response, "Request body can not be empty", Status._400);
            return;
        } else if (StringUtils.isBlank(saveAccountRequest.getFullName())) {
            sendErrorResponse(response, "Full name can not be empty", Status._400);
            return;
        }

        accountService.saveAccount(saveAccountRequest);
        response.sendStatus(Status._200);
    }

}
