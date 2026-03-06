package com.nulhart.exceptions.game;

public class GameNotFoundException extends RuntimeException{

   public GameNotFoundException() {

    }
    public GameNotFoundException(String message){
       super(message);
    }

}
