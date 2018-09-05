package com.sanctacc.mixtogether.movies.events;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DifferentCodesException extends IllegalStateException {

    public DifferentCodesException(String s) {
        super(s);
    }

}