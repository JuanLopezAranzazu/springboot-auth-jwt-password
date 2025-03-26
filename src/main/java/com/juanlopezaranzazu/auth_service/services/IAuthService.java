package com.juanlopezaranzazu.auth_service.services;

import com.juanlopezaranzazu.auth_service.dtos.*;

public interface IAuthService {
    UserResponse register(RegisterRequest registerRequest); // Registro de usuarios
    AuthResponse login(AuthRequest authRequest); // Inicio de sesión de usuarios
    UserResponse me(); // Obtener usuario
    void changePassword(ChangePasswordRequest changePasswordRequest); // Cambiar contraseña de usuario
    void sendPasswordResetToken(String email); // Enviar correo para recuperar contraseña
    void resetPassword(String token, String newPassword); // Recuperar contraseña
}
