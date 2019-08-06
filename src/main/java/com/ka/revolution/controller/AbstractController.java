package com.ka.revolution.controller;

import com.ka.revolution.model.com.ErrorResponse;
import com.ka.revolution.util.FileUtil;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractController {

    public void sendBadRequestError(final Response response, final String message) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(message);

        response.setStatus(Status._400);
        response.setContentType(MediaType._json);
        response.send(FileUtil.convertObjectToJson(errorResponse));

        log.error(errorResponse.toString());
    }

}
