package com.juanlopezaranzazu.auth_service.config;

import com.juanlopezaranzazu.auth_service.entities.Role;
import com.juanlopezaranzazu.auth_service.entities.User;
import com.juanlopezaranzazu.auth_service.exceptions.RoleNotFoundException;
import com.juanlopezaranzazu.auth_service.repositories.IRoleRepository;
import com.juanlopezaranzazu.auth_service.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(2)
public class AdminInitializer implements CommandLineRunner {
    @Value("${admin.firstName}")
    private String adminFirstName;
    @Value("${admin.lastName}")
    private String adminLastName;
    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.phone}")
    private String adminPhone;
    @Value("${admin.address}")
    private String adminAddress;

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String DEFAULT_ROLE_NAME = "ROLE_ADMIN"; // Nombre del rol por defecto

    public AdminInitializer(IUserRepository userRepository, IRoleRepository roleRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // crear usuario ADMIN por defecto
    @Override
    public void run(String... args) {
        // verificar si ya existe un usuario admin
        Optional<User> adminUser = userRepository.findByEmail(adminEmail);
        if (adminUser.isEmpty()) {
            // obtener el rol admin
            Role role = roleRepository.findByName(DEFAULT_ROLE_NAME)
                    .orElseThrow(() -> new RoleNotFoundException(DEFAULT_ROLE_NAME));

            // Crear un usuario
            User newUser = new User();
            newUser.setFirstName(adminFirstName);
            newUser.setLastName(adminLastName);
            newUser.setEmail(adminEmail);
            // Encriptar contraseña
            newUser.setPassword(passwordEncoder.encode(adminPassword));
            newUser.setPhone(adminPhone);
            newUser.setAddress(adminAddress);
            newUser.setRole(role);
            userRepository.save(newUser);
            System.out.println("Usuario ADMIN creado");
        }
    }
}
