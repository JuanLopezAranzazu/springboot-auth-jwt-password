package com.juanlopezaranzazu.auth_service.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String name) {
        super("El rol con nombre '" + name + "' no fue encontrado.");
    }
}
