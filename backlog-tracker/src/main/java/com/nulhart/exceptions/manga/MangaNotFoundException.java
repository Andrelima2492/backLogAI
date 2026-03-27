package com.nulhart.exceptions.manga;

public class MangaNotFoundException extends RuntimeException {
    public MangaNotFoundException(String message) {
        super(message);
    }

    public MangaNotFoundException(){}
}
