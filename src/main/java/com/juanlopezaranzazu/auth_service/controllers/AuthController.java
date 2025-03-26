package com.juanlopezaranzazu.auth_service.controllers;

import com.juanlopezaranzazu.auth_service.dtos.*;
import com.juanlopezaranzazu.auth_service.services.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService){
        this.authService = authService;
    }

    // Registro de usuarios
    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse savedUser = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Inicio de sesión de usuarios
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    // Obtener usuario
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(authService.me());
    }

    // Cambiar contraseña de usuario
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Map<String, Object> response = new HashMap<>();

        authService.changePassword(changePasswordRequest);
        response.put("message", "La contraseña ha sido cambiada con éxito!");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Enviar correo para recuperar la contraseña
    // Ejemplo de petición: /forgot-password?email=correo@dominio
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        authService.sendPasswordResetToken(email);
        response.put("message", "Correo de recuperación enviado.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Recuperar la contraseña
    // Ejemplo de petición: /reset-password?token=token&newPassword=nuevaContraseña
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        Map<String, Object> response = new HashMap<>();

        authService.resetPassword(token, newPassword);
        response.put("message", "Contraseña cambiada con éxito.");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
