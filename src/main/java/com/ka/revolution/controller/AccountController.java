package com.ka.revolution.controller;

import com.ka.revolution.model.com.request.SaveAccountRequest;
import com.ka.revolution.model.com.request.TransferRequest;
import com.ka.revolution.model.com.response.GetAccountsResponse;
import com.ka.revolution.model.persistence.Account;
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
import java.math.BigDecimal;

@Slf4j
@AllArgsConstructor
public class AccountController extends AbstractController {

    private AccountService accountService;

    @DynExpress(context = "/account", method = RequestMethod.GET)
    public void getAccounts(final Request request, final Response response) {
        final GetAccountsResponse getAccountsResponse = new GetAccountsResponse();
        getAccountsResponse.setAccounts(accountService.getAccounts());

        sendResponse(response, FileUtil.convertObjectToJson(getAccountsResponse));
    }

    @DynExpress(context = "/account/:id", method = RequestMethod.GET)
    public void getAccountById(final Request request, final Response response) {
        final Account foundAccount = findAccount(response, request.getParam(PARAMETER_ID));

        if (foundAccount != null) {
            sendResponse(response, FileUtil.convertObjectToJson(foundAccount));
        }
    }

    @DynExpress(context = "/account", method = RequestMethod.POST)
    public void saveAccount(final Request request, final Response response) throws IOException {
        final SaveAccountRequest saveAccountRequest = FileUtil
                .convertJsonStreamToObject(request.getBody(), SaveAccountRequest.class);
        log.debug(saveAccountRequest.toString());

        if (saveAccountRequest == null) {
            validateRequestBody(response, saveAccountRequest);
        } else if (StringUtils.isBlank(saveAccountRequest.getFullName())) {
            sendErrorResponse(response, "Full name can not be empty", Status._400);
        } else {
            accountService.saveAccount(saveAccountRequest);
            response.sendStatus(Status._200);
        }
    }

    @DynExpress(context = "/account/:id/transfer", method = RequestMethod.POST)
    public void transfer(final Request request, final Response response) throws IOException {
        final TransferRequest sendMoneyRequest = FileUtil
                .convertJsonStreamToObject(request.getBody(), TransferRequest.class);

        if (sendMoneyRequest == null) {
            validateRequestBody(response, sendMoneyRequest);
        } else if (sendMoneyRequest.getDestinationAccountId() == null) {
            sendErrorResponse(response, "Destination account id can not be empty", Status._400);
        } else if (sendMoneyRequest.getAmount() == null || sendMoneyRequest.getAmount().compareTo(BigDecimal.ZERO) != 1) {
            sendErrorResponse(response, "Amount can not be empty or negative", Status._400);
        } else {
            final Account originationAccount = findAccount(response, request.getParam(PARAMETER_ID));
            final Account destinationAccount = findAccount(response, String.valueOf(sendMoneyRequest.getDestinationAccountId()));

            if (originationAccount != null && destinationAccount != null) {
                try {
                    accountService.transfer(originationAccount.getId(), sendMoneyRequest);
                    response.sendStatus(Status._200);
                } catch (IllegalArgumentException exception) {
                    log.warn(exception.getMessage(), exception);
                    sendErrorResponse(response, exception.getMessage(), Status._405);
                }
            }
        }
    }

    private Account findAccount(final Response response, final String id) {
        final Account foundAccount = accountService.findAccountById(Long.valueOf(id));

        if (foundAccount == null) {
            sendErrorResponse(response, "Could not find the account -> id: " + id, Status._404);
            return null;
        }

        return foundAccount;
    }

}
