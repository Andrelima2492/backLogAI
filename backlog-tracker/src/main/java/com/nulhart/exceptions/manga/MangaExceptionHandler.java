package com.nulhart.exceptions.manga;

import com.nulhart.exceptions.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MangaExceptionHandler {
    @ExceptionHandler(MangaNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse onMangaNotExistsException(MangaNotFoundException e){
        return  new ErrorResponse(HttpStatus.NOT_FOUND.value(),e.getMessage());
    }

}
