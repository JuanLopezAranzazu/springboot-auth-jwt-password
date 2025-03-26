package com.juanlopezaranzazu.auth_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Genera automáticamente getters, setters, equals, hashCode y toString
@NoArgsConstructor  // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
@Entity  // Indica que esta clase es una entidad JPA
@Table(name = "users")  // Nombre de la tabla en la base de datos
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Autoincremental
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName; // Nombre del usuario

    @Column(name = "last_name", nullable = false)
    private String lastName; // Apellido del usuario

    @Column(nullable = false, unique = true)
    private String email; // Correo del usuario

    @Column(nullable = false)
    private String password; // Contraseña del usuario

    @Column(nullable = false)
    private String phone; // Número de telefono del usuario

    @Column(nullable = false)
    private String address; // Dirección del usuario

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false) // Relación 1 a muchos con la tabla roles
    private Role role;
}
