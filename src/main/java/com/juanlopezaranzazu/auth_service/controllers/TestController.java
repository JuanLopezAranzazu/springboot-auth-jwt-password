package com.juanlopezaranzazu.auth_service.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String getMessageByUser() {
        return "Mensaje para usuario";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String getMessageByAdmin() {
        return "Mensaje para administrador";
    }
}
