package com.ka.revolution.controller;

import com.ka.revolution.model.com.response.ErrorResponse;
import com.ka.revolution.util.FileUtil;
import express.http.response.Response;
import express.utils.MediaType;
import express.utils.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractController {

    protected static final String PARAMETER_ID = "id";

    protected void validateRequestBody(final Response response, final Object object) {
        if (object == null) {
            sendErrorResponse(response, "Request body can not be empty", Status._400);
        }
    }

    protected void sendResponse(final Response response, final String content) {
        response.setStatus(Status._200);
        response.setContentType(MediaType._json);
        response.send(content);
    }

    protected void sendErrorResponse(final Response response, final String message, final Status status) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorMessage(message);

        response.setStatus(status);
        response.setContentType(MediaType._json);
        response.send(FileUtil.convertObjectToJson(errorResponse));

        log.error(errorResponse.toString());
    }

}
