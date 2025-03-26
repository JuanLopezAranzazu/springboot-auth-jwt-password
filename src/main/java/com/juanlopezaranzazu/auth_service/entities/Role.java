package com.juanlopezaranzazu.auth_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data  // Genera automáticamente getters, setters, equals, hashCode y toString
@NoArgsConstructor  // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
@Entity  // Indica que esta clase es una entidad JPA
@Table(name = "roles")  // Nombre de la tabla en la base de datos
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincremental
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Nombre del rol

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;
}
