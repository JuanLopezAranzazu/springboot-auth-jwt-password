package com.juanlopezaranzazu.auth_service.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("El usuario con ID " + id + " no fue encontrado.");
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
