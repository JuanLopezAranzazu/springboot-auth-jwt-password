package com.juanlopezaranzazu.auth_service.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("El usuario con correo '" + email + "' ya existe.");
    }
}
