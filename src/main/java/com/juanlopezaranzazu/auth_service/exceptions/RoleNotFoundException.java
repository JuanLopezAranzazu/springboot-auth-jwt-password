package com.juanlopezaranzazu.auth_service.exceptions;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long id) {
        super("El rol con ID " + id + " no fue encontrado.");
    }

    public RoleNotFoundException(String name) {
        super("El rol con nombre '" + name + "' no fue encontrado.");
    }
}
