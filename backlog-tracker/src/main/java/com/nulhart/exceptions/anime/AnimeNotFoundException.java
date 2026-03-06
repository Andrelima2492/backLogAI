package com.nulhart.exceptions.anime;

public class AnimeNotFoundException extends RuntimeException {
    public AnimeNotFoundException() {
        super();
    }

    public AnimeNotFoundException(String message){
        super(message);
    }
}
