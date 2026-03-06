package com.nulhart.exceptions.anime;

import com.nulhart.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AnimeExceptionHandler {
    @ExceptionHandler(AnimeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onAnimeNotExistsException(AnimeNotFoundException e){
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
