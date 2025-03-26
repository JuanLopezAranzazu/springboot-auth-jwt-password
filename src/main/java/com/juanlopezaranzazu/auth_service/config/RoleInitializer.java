package com.juanlopezaranzazu.auth_service.config;

import com.juanlopezaranzazu.auth_service.entities.Role;
import com.juanlopezaranzazu.auth_service.repositories.IRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(1)
public class RoleInitializer implements CommandLineRunner {

    private final IRoleRepository roleRepository;

    public RoleInitializer(IRoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    // roles de usuario y administrador
    @Override
    public void run(String... args) throws Exception {
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ADMIN");
    }

    // crear los roles por defecto
    private void createRoleIfNotExists(String roleName) {
        Optional<Role> role = roleRepository.findByName(roleName);
        if (role.isEmpty()) {
            Role newRole = new Role();
            newRole.setName(roleName);
            roleRepository.save(newRole);
            System.out.println("Rol creado: " + roleName);
        }
    }
}