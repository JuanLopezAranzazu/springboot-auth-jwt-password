package com.juanlopezaranzazu.auth_service.services;

public interface IEmailService {
    void sendEmail(String to, String subject, String text); // Enviar correo
}
