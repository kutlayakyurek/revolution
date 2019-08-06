package com.ka.revolution.controller;

import com.ka.revolution.model.com.ErrorResponse;
import com.ka.revolution.model.com.SaveAccountRequest;
import com.ka.revolution.model.persistence.Account;
import com.ka.revolution.service.AccountService;
import com.ka.revolution.util.FileUtil;
import express.DynExpress;
import express.http.RequestMethod;
import express.http.request.Request;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class AccountController extends AbstractController {

    private AccountService accountService;

    @DynExpress(context = "/account", method = RequestMethod.POST)
    public void saveAccount(final Request request, final Response response) throws IOException {
        final SaveAccountRequest saveAccountRequest = FileUtil.convertJsonStreamToObject(request.getBody(), SaveAccountRequest.class);
        log.debug(saveAccountRequest.toString());

        if (saveAccountRequest == null) {
            sendBadRequestError(response, "Request body can not be empty");
            return;
        } else if (StringUtils.isBlank(saveAccountRequest.getFullName())) {
            sendBadRequestError(response, "Full name can not be empty");
            return;
        }

        accountService.saveAccount(saveAccountRequest);
        response.sendStatus(Status._200);
    }

}
