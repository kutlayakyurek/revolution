package com.ka.revolution.model.com;

import lombok.Data;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ErrorResponse {

    private String errorMessage;
    private final String date = ZonedDateTime.now(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_ZONED_DATE_TIME);

}
